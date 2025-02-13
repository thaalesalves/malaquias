package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import static java.util.Collections.singleton;
import static me.moirai.discordbot.core.domain.Visibility.PRIVATE;
import static me.moirai.discordbot.core.domain.Visibility.PUBLIC;
import static me.moirai.discordbot.core.domain.adventure.ArtificialIntelligenceModel.GPT35_TURBO;
import static me.moirai.discordbot.core.domain.adventure.GameMode.AUTHOR;
import static me.moirai.discordbot.core.domain.adventure.GameMode.CHAT;
import static me.moirai.discordbot.core.domain.adventure.GameMode.RPG;
import static me.moirai.discordbot.core.domain.adventure.Moderation.DISABLED;
import static me.moirai.discordbot.core.domain.adventure.Moderation.PERMISSIVE;
import static me.moirai.discordbot.core.domain.adventure.Moderation.STRICT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventures;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;
import me.moirai.discordbot.core.domain.adventure.AdventureRepository;
import me.moirai.discordbot.core.domain.adventure.GameMode;
import me.moirai.discordbot.core.domain.adventure.ModelConfigurationFixture;
import me.moirai.discordbot.core.domain.adventure.Moderation;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

public class AdventureRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AdventureRepository repository;

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
    public void updateRememberAdventure() {

        // Given
        String remember = "new value";
        String channelId = "123123123";

        repository.save(AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .version(0)
                .discordChannelId(channelId)
                .build());

        entityManager.flush();
        entityManager.clear();

        // When
        repository.updateRememberByChannelId(remember, channelId);

        // Then
        Adventure updatedAdventure = repository.findByDiscordChannelId(channelId).get();
        assertThat(updatedAdventure.getContextAttributes().getRemember()).isEqualTo(remember);
    }

    @Test
    @Transactional
    public void updateAuthorsNoteAdventure() {

        // Given
        String authorsNote = "new value";
        String channelId = "123123123";

        repository.save(AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .version(0)
                .discordChannelId(channelId)
                .build());

        entityManager.flush();
        entityManager.clear();

        // When
        repository.updateAuthorsNoteByChannelId(authorsNote, channelId);

        // Then
        Adventure updatedAdventure = repository.findByDiscordChannelId(channelId).get();
        assertThat(updatedAdventure.getContextAttributes().getAuthorsNote()).isEqualTo(authorsNote);
    }

    @Test
    @Transactional
    public void updateNudgeAdventure() {

        // Given
        String nudge = "new value";
        String channelId = "123123123";

        repository.save(AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .version(0)
                .discordChannelId(channelId)
                .build());

        entityManager.flush();
        entityManager.clear();

        // When
        repository.updateNudgeByChannelId(nudge, channelId);

        // Then
        Adventure updatedAdventure = repository.findByDiscordChannelId(channelId).get();
        assertThat(updatedAdventure.getContextAttributes().getNudge()).isEqualTo(nudge);
    }

    @Test
    @Transactional
    public void updateBumpAdventure() {

        // Given
        int bumpFrequency = 35;
        String bump = "new value";
        String channelId = "123123123";

        repository.save(AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .version(0)
                .discordChannelId(channelId)
                .build());

        entityManager.flush();
        entityManager.clear();

        // When
        repository.updateBumpByChannelId(bump, bumpFrequency, channelId);

        // Then
        Adventure updatedAdventure = repository.findByDiscordChannelId(channelId).get();
        assertThat(updatedAdventure.getContextAttributes().getBump()).isEqualTo(bump);
        assertThat(updatedAdventure.getContextAttributes().getBumpFrequency()).isEqualTo(bumpFrequency);
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

    @Test
    public void retrieveAdventureById() {

        // Given
        String adventureId = "234234";
        Adventure adventure = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(adventureId)
                .discordChannelId(adventureId)
                .build());

        // When
        Optional<Adventure> retrievedAdventureOptional = repository.findById(adventureId);

        // Then
        assertThat(retrievedAdventureOptional).isNotNull().isNotEmpty();

        Adventure retrievedAdventure = retrievedAdventureOptional.get();
        assertThat(retrievedAdventure.getId()).isEqualTo(adventure.getId());
        assertThat(retrievedAdventure.getAdventureStart()).isEqualTo(adventure.getAdventureStart());
        assertThat(retrievedAdventure.getDescription()).isEqualTo(adventure.getDescription());
        assertThat(retrievedAdventure.getDiscordChannelId()).isEqualTo(adventure.getDiscordChannelId());
        assertThat(retrievedAdventure.getGameMode()).isEqualTo(adventure.getGameMode());
        assertThat(retrievedAdventure.getName()).isEqualTo(adventure.getName());
        assertThat(retrievedAdventure.getOwnerDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(retrievedAdventure.getPersonaId()).isEqualTo(adventure.getPersonaId());
        assertThat(retrievedAdventure.getVisibility()).isEqualTo(adventure.getVisibility());
        assertThat(retrievedAdventure.getModeration()).isEqualTo(adventure.getModeration());
        assertThat(retrievedAdventure.getWorldId()).isEqualTo(adventure.getWorldId());

        assertThat(retrievedAdventure.getModelConfiguration().getAiModel())
                .isEqualTo(adventure.getModelConfiguration().getAiModel());
        assertThat(retrievedAdventure.getModelConfiguration().getFrequencyPenalty())
                .isEqualTo(adventure.getModelConfiguration().getFrequencyPenalty());
        assertThat(retrievedAdventure.getModelConfiguration().getLogitBias())
                .isEqualTo(adventure.getModelConfiguration().getLogitBias());
        assertThat(retrievedAdventure.getModelConfiguration().getMaxTokenLimit())
                .isEqualTo(adventure.getModelConfiguration().getMaxTokenLimit());
        assertThat(retrievedAdventure.getModelConfiguration().getPresencePenalty())
                .isEqualTo(adventure.getModelConfiguration().getPresencePenalty());
        assertThat(retrievedAdventure.getModelConfiguration().getStopSequences())
                .isEqualTo(adventure.getModelConfiguration().getStopSequences());
        assertThat(retrievedAdventure.getModelConfiguration().getTemperature())
                .isEqualTo(adventure.getModelConfiguration().getTemperature());

        assertThat(retrievedAdventure.getUsersAllowedToRead()).hasSameElementsAs(adventure.getUsersAllowedToRead());
        assertThat(retrievedAdventure.getUsersAllowedToWrite()).hasSameElementsAs(adventure.getUsersAllowedToWrite());
    }

    @Test
    public void retrieveAdventureByChannelId() {

        // Given
        String discordChannelId = "234234";
        Adventure adventure = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .discordChannelId(discordChannelId)
                .build());

        // When
        Optional<Adventure> retrievedAdventureOptional = repository.findByDiscordChannelId(discordChannelId);

        // Then
        assertThat(retrievedAdventureOptional).isNotNull().isNotEmpty();

        Adventure retrievedAdventure = retrievedAdventureOptional.get();
        assertThat(retrievedAdventure.getId()).isEqualTo(adventure.getId());
        assertThat(retrievedAdventure.getAdventureStart()).isEqualTo(adventure.getAdventureStart());
        assertThat(retrievedAdventure.getDescription()).isEqualTo(adventure.getDescription());
        assertThat(retrievedAdventure.getDiscordChannelId()).isEqualTo(adventure.getDiscordChannelId());
        assertThat(retrievedAdventure.getGameMode()).isEqualTo(adventure.getGameMode());
        assertThat(retrievedAdventure.getName()).isEqualTo(adventure.getName());
        assertThat(retrievedAdventure.getOwnerDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(retrievedAdventure.getPersonaId()).isEqualTo(adventure.getPersonaId());
        assertThat(retrievedAdventure.getVisibility()).isEqualTo(adventure.getVisibility());
        assertThat(retrievedAdventure.getModeration()).isEqualTo(adventure.getModeration());
        assertThat(retrievedAdventure.getWorldId()).isEqualTo(adventure.getWorldId());

        assertThat(retrievedAdventure.getModelConfiguration().getAiModel())
                .isEqualTo(adventure.getModelConfiguration().getAiModel());
        assertThat(retrievedAdventure.getModelConfiguration().getFrequencyPenalty())
                .isEqualTo(adventure.getModelConfiguration().getFrequencyPenalty());
        assertThat(retrievedAdventure.getModelConfiguration().getLogitBias())
                .isEqualTo(adventure.getModelConfiguration().getLogitBias());
        assertThat(retrievedAdventure.getModelConfiguration().getMaxTokenLimit())
                .isEqualTo(adventure.getModelConfiguration().getMaxTokenLimit());
        assertThat(retrievedAdventure.getModelConfiguration().getPresencePenalty())
                .isEqualTo(adventure.getModelConfiguration().getPresencePenalty());
        assertThat(retrievedAdventure.getModelConfiguration().getStopSequences())
                .isEqualTo(adventure.getModelConfiguration().getStopSequences());
        assertThat(retrievedAdventure.getModelConfiguration().getTemperature())
                .isEqualTo(adventure.getModelConfiguration().getTemperature());

        assertThat(retrievedAdventure.getUsersAllowedToRead()).hasSameElementsAs(adventure.getUsersAllowedToRead());
        assertThat(retrievedAdventure.getUsersAllowedToWrite()).hasSameElementsAs(adventure.getUsersAllowedToWrite());
    }

    @Test
    public void emptyResultWhenAssetDoesntExistGettingByChannelId() {

        // Given
        String adventureId = "WRLDID";

        // When
        Optional<Adventure> retrievedAdventureOptional = repository.findByDiscordChannelId(adventureId);

        // Then
        assertThat(retrievedAdventureOptional).isNotNull().isEmpty();
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParameters() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("580485734")
                        .usersAllowedToRead(singleton(ownerDiscordId))
                        .build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("580485734")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParametersAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini()
                        .aiModel(GPT35_TURBO)
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParametersDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini()
                        .aiModel(GPT35_TURBO)
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(2).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchAdventureOrderByNameAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini()
                        .aiModel(GPT35_TURBO)
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("name")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchAdventureOrderByNameDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini()
                        .aiModel(GPT35_TURBO)
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(2).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureOrderByAiModelAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("ASC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchAdventureOrderByAiModelDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureOrderByModerationAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .moderation(STRICT)
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .moderation(PERMISSIVE)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .moderation(PERMISSIVE)
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("moderation")
                .direction("ASC")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt354k.getName());
        assertThat(adventures.get(2).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchAdventureOrderByModerationDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("DESC")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByAiModel() {

        // Given
        String ownerDiscordId = "586678721356875";
        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .model("GPT4_MINI")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventure_whenReadAccess_andFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .visibility("PRIVATE")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventure_whenWriteAccess_andFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.privateMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .visibility("PRIVATE")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByName() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini()
                        .aiModel(GPT35_TURBO)
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByModeration() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .moderation(STRICT)
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .moderation(PERMISSIVE)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .moderation(PERMISSIVE)
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .moderation("PERMISSIVE")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchAdventures_whenFilterByWorldId_andReaderOnly_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String worldId = "WRLD";
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .worldId(worldId)
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .worldId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .world(worldId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchAdventures_whenFilterByPersonaId_andReaderOnly_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String personaId = "strict";
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .personaId(personaId)
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .personaId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .persona(personaId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParametersShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("580485734")
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("580485734")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParametersAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini()
                        .aiModel(GPT35_TURBO)
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParametersDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini()
                        .aiModel(GPT35_TURBO)
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchAdventureOrderByNameAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini()
                        .aiModel(GPT35_TURBO)
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("name")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchAdventureOrderByNameDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini()
                        .aiModel(GPT35_TURBO)
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureOrderByAiModelAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("ASC")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureOrderByAiModelDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureOrderByModerationAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .moderation(STRICT)
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .moderation(PERMISSIVE)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .moderation(PERMISSIVE)
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("moderation")
                .direction("ASC")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchAdventureOrderByModerationDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("DESC")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByAiModelShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini()
                        .aiModel(GPT35_TURBO)
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .model("GPT35_TURBO")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchAdventureFilterByNameShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini()
                        .aiModel(GPT35_TURBO)
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByModerationShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .moderation(STRICT)
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .moderation(PERMISSIVE)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .moderation(PERMISSIVE)
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .moderation("PERMISSIVE")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchAdventureFilterByGameMode() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .moderation(STRICT)
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .gameMode(CHAT)
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .moderation(PERMISSIVE)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .gameMode(RPG)
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .moderation(PERMISSIVE)
                .discordChannelId("CHNLID3")
                .gameMode(AUTHOR)
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .gameMode("RPG")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByGameModeShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .moderation(STRICT)
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .gameMode(AUTHOR)
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .moderation(PERMISSIVE)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .gameMode(CHAT)
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(singleton(ownerDiscordId))
                        .build())
                .moderation(PERMISSIVE)
                .discordChannelId("CHNLID3")
                .gameMode(RPG)
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .gameMode("CHAT")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByOwner() {

        // Given
        String ownerDiscordId = "586678721358363";
        Adventure gpt4Omni = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .moderation(STRICT)
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .gameMode(AUTHOR)
                .build();

        Adventure gpt4Mini = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .moderation(PERMISSIVE)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .gameMode(CHAT)
                .build();

        Adventure gpt354k = AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 3")
                .moderation(PERMISSIVE)
                .discordChannelId("CHNLID3")
                .gameMode(RPG)
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .ownerDiscordId(ownerDiscordId)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventures_whenFilterByWorldId_andWriterOnly_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String worldId = "WRLD";
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .worldId(worldId)
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .worldId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .world(worldId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchAdventures_whenFilterByPersonaId_andWriterOnly_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String personaId = "strict";
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .personaId(personaId)
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .personaId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .persona(personaId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenNoFilters_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";

        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility(PUBLIC)
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility(PUBLIC)
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByName_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String nameToSearch = "nameToBeSearched";
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility(PUBLIC)
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name(nameToSearch)
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility(PUBLIC)
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .name(nameToSearch)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        Visibility visibility = PUBLIC;
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility(visibility)
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility(PRIVATE)
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .visibility(visibility.name())
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByGameMode_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        GameMode gameMode = CHAT;
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .gameMode(gameMode)
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility(PRIVATE)
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .gameMode(gameMode.name())
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByAiModel_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String model = "GPT4_OMNI";
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility(PRIVATE)
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .model(model)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByModeration_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        Moderation moderation = STRICT;
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .moderation(moderation)
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .moderation(DISABLED)
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .moderation(moderation.name())
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByWorldId_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String worldId = "WRLD";
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .worldId(worldId)
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .worldId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .world(worldId)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByPersonaId_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String personaId = "strict";
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .personaId(personaId)
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .personaId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .persona(personaId)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByOwnerId_thenReturnResults() {

        // Given
        String ownerDiscordId = "1234123";
        Adventure gpt4Omni = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build());

        Adventure gpt4Mini = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .ownerDiscordId(ownerDiscordId)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void adventure_whenChannelIdIsProvided_thenReturnGameMode() {

        // Given
        String discordChannelId = "1234";
        Adventure adventure = jpaRepository.save(AdventureFixture.publicMultiplayerAdventure()
                .discordChannelId(discordChannelId)
                .build());

        // When
        String gameMode = repository.getGameModeByDiscordChannelId(discordChannelId);

        // Then
        assertThat(gameMode).isNotNull()
                .isNotEmpty()
                .isEqualTo(adventure.getGameMode().name());
    }
}
