package me.moirai.discordbot.infrastructure.outbound.persistence.world;

import static me.moirai.discordbot.core.domain.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;
import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldDomainRepository;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

public class WorldDomainRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WorldDomainRepository repository;

    @Autowired
    private WorldJpaRepository jpaRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private WorldLorebookEntryJpaRepository lorebookEntryJpaRepository;

    @BeforeEach
    public void before() {
        lorebookEntryJpaRepository.deleteAllInBatch();
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void createWorld() {

        // Given
        World world = WorldFixture.privateWorld()
                .id(null)
                .build();

        // When
        World createdWorld = repository.save(world);

        // Then
        assertThat(createdWorld).isNotNull();

        assertThat(createdWorld.getCreationDate()).isNotNull();
        assertThat(createdWorld.getLastUpdateDate()).isNotNull();

        assertThat(createdWorld.getName()).isEqualTo(world.getName());
        assertThat(createdWorld.getVisibility()).isEqualTo(world.getVisibility());
        assertThat(createdWorld.getUsersAllowedToWrite()).hasSameElementsAs(world.getUsersAllowedToWrite());
        assertThat(createdWorld.getUsersAllowedToRead()).hasSameElementsAs(world.getUsersAllowedToRead());
    }

    @Test
    public void retrieveWorldById() {

        // Given
        World world = repository.save(WorldFixture.privateWorld()
                .id(null)
                .build());

        // When
        Optional<World> retrievedWorldOptional = repository.findById(world.getId());

        // Then
        assertThat(retrievedWorldOptional).isNotNull().isNotEmpty();

        World retrievedWorld = retrievedWorldOptional.get();
        assertThat(retrievedWorld.getId()).isEqualTo(world.getId());
    }

    @Test
    public void emptyResultWhenAssetDoesntExist() {

        // Given
        String worldId = "WRLDID";

        // When
        Optional<World> retrievedWorldOptional = repository.findById(worldId);

        // Then
        assertThat(retrievedWorldOptional).isNotNull().isEmpty();
    }

    @Test
    public void deleteWorld() {

        // Given
        World world = repository.save(WorldFixture.privateWorld()
                .id(null)
                .build());

        // When
        repository.deleteById(world.getId());

        // Then
        assertThat(repository.findById(world.getId())).isNotNull().isEmpty();
    }

    @Test
    public void updateWorld() {

        // Given
        World originalWorld = repository.save(WorldFixture.privateWorld()
                .id(null)
                .build());

        World worldToUbeUpdated = WorldFixture.privateWorld()
                .id(originalWorld.getId())
                .visibility(PUBLIC)
                .version(originalWorld.getVersion())
                .build();

        // When
        World updatedWorld = repository.save(worldToUbeUpdated);

        // Then
        assertThat(originalWorld.getVersion()).isZero();
        assertThat(updatedWorld.getVersion()).isOne();
    }

    @Test
    @Transactional
    public void deleteWorld_whenIsFavorite_thenDeleteFavorites() {

        // Given
        String userId = "1234";
        World originalWorld = repository.save(WorldFixture.privateWorld()
                .id(null)
                .build());

        FavoriteEntity favorite = favoriteRepository.save(FavoriteEntity.builder()
                .playerDiscordId(userId)
                .assetId(originalWorld.getId())
                .assetType("persona")
                .build());

        // When
        repository.deleteById(originalWorld.getId());

        // Then
        assertThat(repository.findById(originalWorld.getId())).isNotNull().isEmpty();
        assertThat(favoriteRepository.existsById(favorite.getId())).isFalse();
    }
}