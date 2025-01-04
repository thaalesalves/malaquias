package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureById;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;

@UseCaseHandler
public class GetAdventureByIdHandler extends AbstractUseCaseHandler<GetAdventureById, GetAdventureResult> {

    private static final String ADVENTURE_NOT_FOUND = "Adventure to be viewed was not found";
    private static final String PERMISSION_VIEW_DENIED = "User does not have permission to view this adventure";

    private final AdventureQueryRepository queryRepository;

    public GetAdventureByIdHandler(AdventureQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Override
    public GetAdventureResult execute(GetAdventureById query) {

        Adventure adventure = queryRepository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException(ADVENTURE_NOT_FOUND));

        if (!adventure.canUserRead(query.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(PERMISSION_VIEW_DENIED);
        }

        return mapResult(adventure);
    }

    private GetAdventureResult mapResult(Adventure adventure) {

        return GetAdventureResult.builder()
                .id(adventure.getId())
                .name(adventure.getName())
                .worldId(adventure.getWorldId())
                .personaId(adventure.getPersonaId())
                .visibility(adventure.getVisibility().name())
                .aiModel(adventure.getModelConfiguration().getAiModel().toString())
                .moderation(adventure.getModeration().name())
                .maxTokenLimit(adventure.getModelConfiguration().getMaxTokenLimit())
                .temperature(adventure.getModelConfiguration().getTemperature())
                .frequencyPenalty(adventure.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(adventure.getModelConfiguration().getPresencePenalty())
                .stopSequences(adventure.getModelConfiguration().getStopSequences())
                .logitBias(adventure.getModelConfiguration().getLogitBias())
                .usersAllowedToWrite(adventure.getUsersAllowedToWrite())
                .usersAllowedToRead(adventure.getUsersAllowedToRead())
                .ownerDiscordId(adventure.getOwnerDiscordId())
                .creationDate(adventure.getCreationDate())
                .lastUpdateDate(adventure.getLastUpdateDate())
                .description(adventure.getDescription())
                .adventureStart(adventure.getAdventureStart())
                .discordChannelId(adventure.getDiscordChannelId())
                .gameMode(adventure.getGameMode().name())
                .authorsNote(adventure.getContextAttributes().getAuthorsNote())
                .nudge(adventure.getContextAttributes().getNudge())
                .remember(adventure.getContextAttributes().getRemember())
                .bump(adventure.getContextAttributes().getBump())
                .bumpFrequency(adventure.getContextAttributes().getBumpFrequency())
                .isMultiplayer(adventure.isMultiplayer())
                .build();
    }
}
