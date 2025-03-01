package me.moirai.discordbot.core.application.usecase.adventure;

import static me.moirai.discordbot.core.domain.Visibility.PRIVATE;
import static me.moirai.discordbot.core.domain.Visibility.PUBLIC;
import static me.moirai.discordbot.core.domain.adventure.ArtificialIntelligenceModel.fromString;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventure;
import me.moirai.discordbot.core.application.usecase.adventure.result.UpdateAdventureResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureRepository;
import me.moirai.discordbot.core.domain.adventure.GameMode;
import me.moirai.discordbot.core.domain.adventure.Moderation;

@UseCaseHandler
public class UpdateAdventureHandler extends AbstractUseCaseHandler<UpdateAdventure, UpdateAdventureResult> {

    private static final String USER_NO_PERMISSION = "User does not have permission to delete adventure";
    private static final String ADVENTURE_NOT_FOUND = "Adventure to be updated was not found";
    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Adventure ID cannot be null or empty";

    private final AdventureRepository repository;

    public UpdateAdventureHandler(AdventureRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validate(UpdateAdventure command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public UpdateAdventureResult execute(UpdateAdventure command) {

        Adventure adventure = repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException(ADVENTURE_NOT_FOUND));

        if (!adventure.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_NO_PERMISSION);
        }

        if (isNotBlank(command.getName())) {
            adventure.updateName(command.getName());
        }

        if (isNotBlank(command.getWorldId())) {
            adventure.updateWorld(command.getWorldId());
        }

        if (isNotBlank(command.getPersonaId())) {
            adventure.updatePersona(command.getPersonaId());
        }

        if (isNotBlank(command.getAiModel())) {
            adventure.updateAiModel(fromString(command.getAiModel()));
        }

        if (isNotBlank(command.getModeration())) {
            adventure.updateModeration(Moderation.fromString(command.getModeration()));
        }

        if (isNotBlank(command.getDiscordChannelId())) {
            adventure.updateDiscordChannel(command.getDiscordChannelId());
        }

        if (isNotBlank(command.getGameMode())) {
            adventure.updateGameMode(GameMode.fromString(command.getGameMode()));
        }

        if (command.getTemperature() != null) {
            adventure.updateTemperature(command.getTemperature());
        }

        if (command.getFrequencyPenalty() != null) {
            adventure.updateFrequencyPenalty(command.getFrequencyPenalty());
        }

        if (command.getPresencePenalty() != null) {
            adventure.updatePresencePenalty(command.getPresencePenalty());
        }

        if (isNotBlank(command.getAdventureStart())) {
            adventure.updateAdventureStart(command.getAdventureStart());
        }

        if (isNotBlank(command.getDescription())) {
            adventure.updateDescription(command.getDescription());
        }

        if (isNotBlank(command.getNudge())) {
            adventure.updateNudge(command.getNudge());
        }

        if (isNotBlank(command.getRemember())) {
            adventure.updateRemember(command.getRemember());
        }

        if (isNotBlank(command.getAdventureStart())) {
            adventure.updateAdventureStart(command.getAdventureStart());
        }

        if (isNotBlank(command.getAuthorsNote())) {
            adventure.updateAuthorsNote(command.getAuthorsNote());
        }

        if (isNotBlank(command.getBump())) {
            adventure.updateBump(command.getBump());
            adventure.updateBumpFrequency(command.getBumpFrequency());
        }

        if (command.isMultiplayer()) {
            adventure.makeMultiplayer();
        } else {
            adventure.makeSinglePlayer();
        }

        updateStopSequences(command, adventure);
        updateLogitBias(command, adventure);
        updatePermissions(command, adventure);

        return mapResult(repository.save(adventure));
    }

    private void updatePermissions(UpdateAdventure command, Adventure adventure) {

        if (isNotBlank(command.getVisibility())) {
            if (command.getVisibility().equalsIgnoreCase(PUBLIC.name())) {
                adventure.makePublic();
            } else if (command.getVisibility().equalsIgnoreCase(PRIVATE.name())) {
                adventure.makePrivate();
            }
        }

        emptyIfNull(command.getUsersAllowedToReadToAdd())
                .forEach(adventure::addReaderUser);

        emptyIfNull(command.getUsersAllowedToWriteToAdd())
                .forEach(adventure::addWriterUser);

        emptyIfNull(command.getUsersAllowedToReadToRemove())
                .forEach(adventure::removeReaderUser);

        emptyIfNull(command.getUsersAllowedToWriteToRemove())
                .forEach(adventure::removeWriterUser);
    }

    private void updateLogitBias(UpdateAdventure command, Adventure adventure) {

        emptyIfNull(command.getLogitBiasToAdd())
                .entrySet()
                .stream()
                .forEach(entry -> adventure.addLogitBias(entry.getKey(), entry.getValue()));

        emptyIfNull(command.getLogitBiasToRemove())
                .forEach(adventure::removeLogitBias);
    }

    private void updateStopSequences(UpdateAdventure command, Adventure adventure) {

        emptyIfNull(command.getStopSequencesToAdd())
                .stream()
                .forEach(adventure::addStopSequence);

        emptyIfNull(command.getStopSequencesToRemove())
                .forEach(adventure::removeStopSequence);
    }

    private UpdateAdventureResult mapResult(Adventure savedAdventure) {

        return UpdateAdventureResult.build(savedAdventure.getLastUpdateDate());
    }
}
