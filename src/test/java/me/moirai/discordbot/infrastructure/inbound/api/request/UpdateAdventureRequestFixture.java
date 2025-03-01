package me.moirai.discordbot.infrastructure.inbound.api.request;

import java.util.Collections;

import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;

public class UpdateAdventureRequestFixture {

    public static UpdateAdventureRequest sample() {

        Adventure adventure = AdventureFixture.privateMultiplayerAdventure().build();
        UpdateAdventureRequest request = new UpdateAdventureRequest();

        request.setName(adventure.getName());
        request.setPersonaId(adventure.getPersonaId());
        request.setWorldId(adventure.getWorldId());
        request.setDiscordChannelId(adventure.getDiscordChannelId());
        request.setAiModel(adventure.getModelConfiguration().getAiModel().toString());
        request.setStopSequencesToAdd(adventure.getModelConfiguration().getStopSequences());
        request.setStopSequencesToRemove(adventure.getModelConfiguration().getStopSequences());
        request.setLogitBiasToAdd(adventure.getModelConfiguration().getLogitBias());
        request.setLogitBiasToRemove(Collections.singleton("TKN"));
        request.setFrequencyPenalty(adventure.getModelConfiguration().getFrequencyPenalty());
        request.setMaxTokenLimit(adventure.getModelConfiguration().getMaxTokenLimit());
        request.setModeration(adventure.getModeration().name());
        request.setGameMode(adventure.getGameMode().name());
        request.setUsersAllowedToWriteToAdd(Collections.singleton("USRID"));
        request.setUsersAllowedToWriteToRemove(Collections.singleton("USRID"));
        request.setUsersAllowedToReadToAdd(Collections.singleton("USRID"));
        request.setUsersAllowedToReadToRemove(Collections.singleton("USRID"));
        request.setTemperature(1.7);
        request.setVisibility(adventure.getVisibility().name());
        request.setMultiplayer(false);
        request.setAdventureStart(adventure.getAdventureStart());
        request.setNudge(adventure.getContextAttributes().getNudge());
        request.setAuthorsNote(adventure.getContextAttributes().getAuthorsNote());
        request.setRemember(adventure.getContextAttributes().getRemember());
        request.setBump(adventure.getContextAttributes().getBump());
        request.setBumpFrequency(adventure.getContextAttributes().getBumpFrequency());

        return request;
    }
}