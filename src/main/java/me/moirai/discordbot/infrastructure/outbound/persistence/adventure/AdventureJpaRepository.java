package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.infrastructure.outbound.persistence.PaginationRepository;

public interface AdventureJpaRepository
        extends JpaRepository<Adventure, String>, PaginationRepository<Adventure, String> {

    Optional<Adventure> findByDiscordChannelId(String channelId);

    @Query("SELECT cc.gameMode FROM Adventure cc WHERE cc.discordChannelId = :channelId")
    String getGameModeByDiscordChannelId(String channelId);

    @Modifying
    @Query("UPDATE Adventure a SET a.contextAttributes.remember = :remember WHERE a.discordChannelId = :discordChannelId")
    void updateRememberByChannelId(String remember, String discordChannelId);

    @Modifying
    @Query("UPDATE Adventure a SET a.contextAttributes.authorsNote = :authorsNote WHERE a.discordChannelId = :discordChannelId")
    void updateAuthorsNoteByChannelId(String authorsNote, String discordChannelId);

    @Modifying
    @Query("UPDATE Adventure a SET a.contextAttributes.nudge = :nudge WHERE a.discordChannelId = :discordChannelId")
    void updateNudgeByChannelId(String nudge, String discordChannelId);

    @Modifying
    @Query("UPDATE Adventure a SET a.contextAttributes.bump = :bump, a.contextAttributes.bumpFrequency = :bumpFrequency WHERE a.discordChannelId = :discordChannelId")
    void updateBumpByChannelId(String bump, int bumpFrequency, String discordChannelId);
}
