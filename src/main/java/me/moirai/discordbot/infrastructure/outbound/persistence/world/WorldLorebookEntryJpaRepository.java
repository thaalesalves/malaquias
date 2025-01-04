package me.moirai.discordbot.infrastructure.outbound.persistence.world;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import me.moirai.discordbot.core.domain.world.WorldLorebookEntry;
import me.moirai.discordbot.infrastructure.outbound.persistence.PaginationRepository;

public interface WorldLorebookEntryJpaRepository
        extends JpaRepository<WorldLorebookEntry, String>,
        PaginationRepository<WorldLorebookEntry, String> {

    @Query(value = "SELECT entry.* FROM world_lorebook entry WHERE :valueToMatch ~ entry.regex AND entry.world_id = :worldId", nativeQuery = true)
    List<WorldLorebookEntry> findAllByNameRegex(String valueToMatch, String worldId);

    @Query(value = "SELECT entry.* FROM world_lorebook entry WHERE entry.player_discord_id = :playerDiscordId AND entry.world_id = :worldId", nativeQuery = true)
    Optional<WorldLorebookEntry> findByPlayerDiscordId(String playerDiscordId, String worldId);

    List<WorldLorebookEntry> findAllByWorldId(String worldId);
}