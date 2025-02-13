package me.moirai.discordbot.core.application.usecase.adventure;

import static me.moirai.discordbot.core.domain.adventure.ArtificialIntelligenceModel.fromString;

import java.util.List;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.port.WorldQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.CreateAdventure;
import me.moirai.discordbot.core.application.usecase.adventure.result.CreateAdventureResult;
import me.moirai.discordbot.core.domain.Permissions;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureRepository;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntry;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntryRepository;
import me.moirai.discordbot.core.domain.adventure.ContextAttributes;
import me.moirai.discordbot.core.domain.adventure.GameMode;
import me.moirai.discordbot.core.domain.adventure.ModelConfiguration;
import me.moirai.discordbot.core.domain.adventure.Moderation;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryRepository;

@UseCaseHandler
public class CreateAdventureHandler extends AbstractUseCaseHandler<CreateAdventure, CreateAdventureResult> {

    private static final String USER_NO_PERMISSION_IN_WORLD = "User does not have permission to view the world to be linked to this adventure";
    private static final String USER_NO_PERMISSION_IN_PERSONA = "User does not have permission to view the persona to be linked to this adventure";
    private static final String WORLD_DOES_NOT_EXIST = "The world to be linked to this adventure does not exist";
    private static final String PERSONA_DOES_NOT_EXIST = "The persona to be linked to this adventure does not exist";

    private final WorldLorebookEntryRepository worldLorebookEntryRepository;
    private final WorldQueryRepository worldQueryRepository;
    private final PersonaQueryRepository personaQueryRepository;
    private final AdventureRepository repository;
    private final AdventureLorebookEntryRepository lorebookEntryRepository;

    public CreateAdventureHandler(
            WorldLorebookEntryRepository worldLorebookEntryRepository,
            WorldQueryRepository worldQueryRepository,
            PersonaQueryRepository personaQueryRepository,
            AdventureRepository repository,
            AdventureLorebookEntryRepository lorebookEntryRepository) {

        this.worldLorebookEntryRepository = worldLorebookEntryRepository;
        this.worldQueryRepository = worldQueryRepository;
        this.personaQueryRepository = personaQueryRepository;
        this.repository = repository;
        this.lorebookEntryRepository = lorebookEntryRepository;
    }

    @Override
    public CreateAdventureResult execute(CreateAdventure command) {

        World world = getWorldTobeLinked(command);
        Persona persona = getPersonaToBeLinked(command);

        ModelConfiguration modelConfiguration = buildModelConfiguration(command);
        Permissions permissions = buildPermissions(command);
        ContextAttributes contextAttributes = buildContextAttributes(command);

        Adventure adventure = repository.save(Adventure.builder()
                .modelConfiguration(modelConfiguration)
                .permissions(permissions)
                .name(command.getName())
                .personaId(persona.getId())
                .worldId(world.getId())
                .discordChannelId(command.getDiscordChannelId())
                .gameMode(GameMode.fromString(command.getGameMode()))
                .visibility(Visibility.fromString(command.getVisibility()))
                .moderation(Moderation.fromString(command.getModeration()))
                .isMultiplayer(command.isMultiplayer())
                .adventureStart(world.getAdventureStart())
                .contextAttributes(contextAttributes)
                .description(command.getDescription())
                .build());

        List<AdventureLorebookEntry> lorebook = buildLorebook(world, adventure);
        lorebook.stream().forEach(lorebookEntryRepository::save);

        return CreateAdventureResult.build(adventure.getId());
    }

    private Persona getPersonaToBeLinked(CreateAdventure command) {

        Persona persona = personaQueryRepository.findById(command.getPersonaId())
                .orElseThrow(() -> new AssetNotFoundException(PERSONA_DOES_NOT_EXIST));

        if (!persona.canUserRead(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_NO_PERMISSION_IN_PERSONA);
        }

        return persona;
    }

    private World getWorldTobeLinked(CreateAdventure command) {

        World world = worldQueryRepository.findById(command.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException(WORLD_DOES_NOT_EXIST));

        if (!world.canUserRead(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_NO_PERMISSION_IN_WORLD);
        }

        return world;
    }

    private List<AdventureLorebookEntry> buildLorebook(World world, Adventure adventure) {

        return worldLorebookEntryRepository.findAllByWorldId(world.getId()).stream()
                .map(worldEntry -> AdventureLorebookEntry.builder()
                        .name(worldEntry.getName())
                        .regex(worldEntry.getRegex())
                        .description(worldEntry.getDescription())
                        .playerDiscordId(worldEntry.getPlayerDiscordId())
                        .isPlayerCharacter(worldEntry.isPlayerCharacter())
                        .adventureId(adventure.getId())
                        .build())
                .toList();
    }

    private ContextAttributes buildContextAttributes(CreateAdventure command) {

        return ContextAttributes.builder()
                .authorsNote(command.getAuthorsNote())
                .nudge(command.getNudge())
                .remember(command.getRemember())
                .bump(command.getBump())
                .bumpFrequency(command.getBumpFrequency())
                .build();
    }

    private Permissions buildPermissions(CreateAdventure command) {

        return Permissions.builder()
                .ownerDiscordId(command.getRequesterDiscordId())
                .usersAllowedToRead(command.getUsersAllowedToRead())
                .usersAllowedToWrite(command.getUsersAllowedToWrite())
                .build();
    }

    private ModelConfiguration buildModelConfiguration(CreateAdventure command) {

        return ModelConfiguration.builder()
                .aiModel(fromString(command.getAiModel()))
                .frequencyPenalty(command.getFrequencyPenalty())
                .presencePenalty(command.getPresencePenalty())
                .temperature(command.getTemperature())
                .logitBias(command.getLogitBias())
                .maxTokenLimit(command.getMaxTokenLimit())
                .stopSequences(command.getStopSequences())
                .build();
    }
}
