package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureByChannelId;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureRepository;

@UseCaseHandler
public class GetAdventureByChannelIdHandler
        extends AbstractUseCaseHandler<GetAdventureByChannelId, GetAdventureResult> {

    private static final String ADVENTURE_NOT_FOUND = "No adventures exist for this channel";
    private static final String USER_NO_PERMISSION = "User does not have permission to view adventure";

    private final AdventureRepository queryRepository;

    public GetAdventureByChannelIdHandler(AdventureRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Override
    public GetAdventureResult execute(GetAdventureByChannelId useCase) {

        Adventure adventure = queryRepository.findByDiscordChannelId(useCase.getChannelId())
                .orElseThrow(() -> new AssetNotFoundException(ADVENTURE_NOT_FOUND));

        if (!adventure.canUserRead(useCase.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_NO_PERMISSION);
        }

        return toResult(adventure);
    }

    private GetAdventureResult toResult(Adventure adventure) {

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
