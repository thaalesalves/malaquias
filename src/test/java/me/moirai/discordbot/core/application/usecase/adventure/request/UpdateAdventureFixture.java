package me.moirai.discordbot.core.application.usecase.adventure.request;

import java.util.Collections;

import org.assertj.core.util.Maps;

import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;

public class UpdateAdventureFixture {

    public static UpdateAdventure.Builder sample() {

        Adventure adventure = AdventureFixture.privateMultiplayerAdventure().build();
        return UpdateAdventure.builder()
                .id(adventure.getId())
                .name(adventure.getName())
                .description(adventure.getDescription())
                .worldId(adventure.getWorldId())
                .personaId(adventure.getPersonaId())
                .discordChannelId(adventure.getDiscordChannelId())
                .visibility(adventure.getVisibility().name())
                .aiModel(adventure.getModelConfiguration().getAiModel().toString())
                .moderation(adventure.getModeration().name())
                .maxTokenLimit(adventure.getModelConfiguration().getMaxTokenLimit())
                .temperature(adventure.getModelConfiguration().getTemperature())
                .frequencyPenalty(adventure.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(adventure.getModelConfiguration().getPresencePenalty())
                .stopSequencesToAdd(adventure.getModelConfiguration().getStopSequences())
                .stopSequencesToRemove(adventure.getModelConfiguration().getStopSequences())
                .logitBiasToAdd(Maps.newHashMap("TKNID", 99D))
                .logitBiasToRemove(Collections.singleton("TKN"))
                .usersAllowedToWriteToAdd(Collections.singleton("USRID"))
                .usersAllowedToWriteToRemove(Collections.singleton("USRID"))
                .usersAllowedToReadToAdd(Collections.singleton("USRID"))
                .usersAllowedToReadToRemove(Collections.singleton("USRID"))
                .gameMode(adventure.getGameMode().name())
                .requesterDiscordId(adventure.getOwnerDiscordId())
                .nudge(adventure.getContextAttributes().getNudge())
                .remember(adventure.getContextAttributes().getRemember())
                .authorsNote(adventure.getContextAttributes().getAuthorsNote())
                .bump(adventure.getContextAttributes().getBump())
                .bumpFrequency(adventure.getContextAttributes().getBumpFrequency())
                .isMultiplayer(adventure.isMultiplayer())
                .adventureStart(adventure.getAdventureStart());
    }
}
