package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntry;
import me.moirai.discordbot.infrastructure.outbound.persistence.PaginationRepository;

public interface AdventureLorebookEntryJpaRepository
        extends JpaRepository<AdventureLorebookEntry, String>,
        PaginationRepository<AdventureLorebookEntry, String> {

    @Query(value = "SELECT entry.* FROM adventure_lorebook entry WHERE :valueToMatch ~ entry.regex AND entry.adventure_id = :adventureId", nativeQuery = true)
    List<AdventureLorebookEntry> findAllByNameRegex(String valueToMatch, String adventureId);

    @Query(value = "SELECT entry.* FROM adventure_lorebook entry WHERE entry.player_discord_id = :playerDiscordId AND entry.adventure_id = :adventureId", nativeQuery = true)
    Optional<AdventureLorebookEntry> findByPlayerDiscordId(String playerDiscordId, String adventureId);

    List<AdventureLorebookEntry> findAllByAdventure(Adventure adventure);
}