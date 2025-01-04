package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;
import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

public class AdventureDomainRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AdventureDomainRepository repository;

    @Autowired
    private AdventureJpaRepository jpaRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private AdventureLorebookEntryJpaRepository lorebookEntryJpaRepository;

    @BeforeEach
    public void before() {
        lorebookEntryJpaRepository.deleteAllInBatch();
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void createAdventure() {

        // Given
        Adventure adventure = AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .build();

        // When
        Adventure createdAdventure = repository.save(adventure);

        // Then
        assertThat(createdAdventure).isNotNull();

        assertThat(createdAdventure.getCreationDate()).isNotNull();
        assertThat(createdAdventure.getLastUpdateDate()).isNotNull();

        assertThat(createdAdventure.getModelConfiguration().getAiModel().toString())
                .isEqualTo((adventure.getModelConfiguration().getAiModel().toString()));

        assertThat(createdAdventure.getModelConfiguration().getFrequencyPenalty())
                .isEqualTo((adventure.getModelConfiguration().getFrequencyPenalty()));

        assertThat(createdAdventure.getModelConfiguration().getPresencePenalty())
                .isEqualTo((adventure.getModelConfiguration().getPresencePenalty()));

        assertThat(createdAdventure.getModelConfiguration().getTemperature())
                .isEqualTo((adventure.getModelConfiguration().getTemperature()));

        assertThat(createdAdventure.getModelConfiguration().getLogitBias())
                .isEqualTo((adventure.getModelConfiguration().getLogitBias()));

        assertThat(createdAdventure.getModelConfiguration().getMaxTokenLimit())
                .isEqualTo((adventure.getModelConfiguration().getMaxTokenLimit()));

        assertThat(createdAdventure.getModelConfiguration().getStopSequences())
                .isEqualTo((adventure.getModelConfiguration().getStopSequences()));

    }

    @Test
    public void retrieveAdventureById() {

        // Given
        Adventure adventure = repository.save(AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .build());

        // When
        Optional<Adventure> retrievedAdventureOptional = repository.findById(adventure.getId());

        // Then
        assertThat(retrievedAdventureOptional).isNotNull().isNotEmpty();

        Adventure retrievedAdventure = retrievedAdventureOptional.get();
        assertThat(retrievedAdventure.getId()).isEqualTo(adventure.getId());
    }

    @Test
    public void emptyResultWhenAssetDoesntExist() {

        // Given
        String adventureId = "WRLDID";

        // When
        Optional<Adventure> retrievedAdventureOptional = repository.findById(adventureId);

        // Then
        assertThat(retrievedAdventureOptional).isNotNull().isEmpty();
    }

    @Test
    public void deleteAdventure() {

        // Given
        Adventure adventure = repository.save(AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .build());

        // When
        repository.deleteById(adventure.getId());

        // Then
        assertThat(repository.findById(adventure.getId())).isNotNull().isEmpty();
    }

    @Test
    public void updateAdventure() {

        // Given
        Adventure originalAdventure = repository.save(AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .version(0)
                .build());

        Adventure worldToUbeUpdated = AdventureFixture.privateMultiplayerAdventure()
                .id(originalAdventure.getId())
                .visibility(Visibility.PUBLIC)
                .version(originalAdventure.getVersion())
                .build();

        // When
        Adventure updatedAdventure = repository.save(worldToUbeUpdated);

        // Then
        assertThat(originalAdventure.getVersion()).isZero();
        assertThat(updatedAdventure.getVersion()).isOne();
    }

    @Test
    @Transactional
    public void deleteAdventure_whenIsFavorite_thenDeleteFavorites() {

        // Given
        String userId = "1234";
        Adventure adventure = repository.save(AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .build());

        FavoriteEntity favorite = favoriteRepository.save(FavoriteEntity.builder()
                .playerDiscordId(userId)
                .assetId(adventure.getId())
                .assetType("adventure")
                .build());

        // When
        repository.deleteById(adventure.getId());

        // Then
        assertThat(repository.findById(adventure.getId())).isNotNull().isEmpty();
        assertThat(favoriteRepository.existsById(favorite.getId())).isFalse();
        assertThat(lorebookEntryJpaRepository.findAllByAdventureId(adventure.getId())).isEmpty();
    }
}
