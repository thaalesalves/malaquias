package me.moirai.discordbot.core.domain.adventure;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventures;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;

public interface AdventureRepository {

    Adventure save(Adventure adventure);

    void deleteById(String id);

    void updateRememberByChannelId(String remember, String channelId);

    void updateAuthorsNoteByChannelId(String authorsNote, String channelId);

    void updateNudgeByChannelId(String nudge, String channelId);

    void updateBumpByChannelId(String bumpContent, int bumpFrequency, String channelId);

    Optional<Adventure> findById(String id);

    SearchAdventuresResult search(SearchAdventures request);

    Optional<Adventure> findByDiscordChannelId(String channelId);

    String getGameModeByDiscordChannelId(String discordChannelId);
}
