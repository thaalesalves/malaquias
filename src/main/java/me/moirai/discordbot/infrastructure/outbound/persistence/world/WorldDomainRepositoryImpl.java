package me.moirai.discordbot.infrastructure.outbound.persistence.world;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldDomainRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@Repository
public class WorldDomainRepositoryImpl implements WorldDomainRepository {

    private final WorldJpaRepository jpaRepository;
    private final FavoriteRepository favoriteRepository;

    public WorldDomainRepositoryImpl(WorldJpaRepository jpaRepository,
            FavoriteRepository favoriteRepository) {

        this.jpaRepository = jpaRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public World save(World world) {

        return jpaRepository.save(world);
    }

    @Override
    public Optional<World> findById(String id) {

        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {

        favoriteRepository.deleteAllByAssetId(id);

        jpaRepository.deleteById(id);
    }
}
