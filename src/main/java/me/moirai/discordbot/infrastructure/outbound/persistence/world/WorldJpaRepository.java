package me.moirai.discordbot.infrastructure.outbound.persistence.world;

import org.springframework.data.jpa.repository.JpaRepository;

import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.infrastructure.outbound.persistence.PaginationRepository;

public interface WorldJpaRepository
        extends JpaRepository<World, String>, PaginationRepository<World, String> {

}