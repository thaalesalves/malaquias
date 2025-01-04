package me.moirai.discordbot.infrastructure.outbound.persistence.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorlds;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldResult;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

public class WorldQueryRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WorldQueryRepositoryImpl repository;

    @Autowired
    private WorldJpaRepository jpaRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void returnAllWorldsWhenSearchingWithoutParameters() {

        // Given
        String ownerDiscordId = "586678721356875";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("580485734")
                        .usersAllowedToRead(list(ownerDiscordId))
                        .build())
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("580485734")
                        .build())
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorlds query = SearchWorlds.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnOnlyWorldsWithReadAccessWhenSearchingWithoutParametersAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorlds query = SearchWorlds.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllWorldsWhenSearchingWithoutParametersDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorlds query = SearchWorlds.builder()
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchWorldOrderByNameAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .sortingField("name")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchWorldOrderByNameDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .sortingField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchWorldFilterByName() {

        // Given
        String ownerDiscordId = "586678721356875";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchWorld_whenReadAccess_andFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String visibilityToSearch = "public";
        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        World gpt4Mini = WorldFixture.publicWorld()
                .id(null)
                .name("Number 2")
                .build();

        World gpt354k = WorldFixture.publicWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .visibility(visibilityToSearch)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchWorld_whenWriteAccess_andFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String visibilityToSearch = "public";
        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        World gpt4Mini = WorldFixture.publicWorld()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build();

        World gpt354k = WorldFixture.publicWorld()
                .id(null)
                .name("Number 3")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .visibility(visibilityToSearch)
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllWorldsWhenSearchingWithoutParametersShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("580485734")
                        .usersAllowedToWrite(list(ownerDiscordId))
                        .build())
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("580485734")
                        .build())
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorlds query = SearchWorlds.builder()
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnOnlyWorldsWithWriteAccessWhenSearchingWithoutParametersAsc() {

        // Given
        String ownerDiscordId = "586678721358363";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(list(ownerDiscordId))
                        .build())
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorlds query = SearchWorlds.builder()
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllWorldsWhenSearchingWithoutParametersDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(list(ownerDiscordId))
                        .build())
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorlds query = SearchWorlds.builder()
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchWorldOrderByNameAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(list(ownerDiscordId))
                        .build())
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .sortingField("name")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchWorldOrderByNameDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(list(ownerDiscordId))
                        .build())
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .sortingField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchWorldFilterByNameShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(list(ownerDiscordId))
                        .build())
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void emptyResultWhenSearchingForWorldWithWriteAccessIfUserHasNoAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        World gpt4Omni = WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(list(ownerDiscordId))
                        .build())
                .build();

        World gpt4Mini = WorldFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .build();

        World gpt354k = WorldFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isEmpty();
    }

    @Test
    public void searcFavoritehWorlds_whenNoFilter_thenReturnAllResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        World gpt4Omni = jpaRepository.save(WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build());

        World gpt4Mini = jpaRepository.save(WorldFixture.publicWorld()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(list(ownerDiscordId))
                        .build())
                .build());

        World gpt354k = jpaRepository.save(WorldFixture.publicWorld()
                .id(null)
                .name("Number 3")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("world")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("world")
                .assetId(gpt4Mini.getId())
                .build();

        FavoriteEntity favorite3 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("world")
                .assetId(gpt354k.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2, favorite3));

        SearchWorlds query = SearchWorlds.builder()
                .requesterDiscordId(ownerDiscordId)
                .favorites(true)
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);
    }

    @Test
    public void searcFavoritehWorlds_whenByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        World gpt4Omni = jpaRepository.save(WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build());

        World gpt4Mini = jpaRepository.save(WorldFixture.publicWorld()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(list(ownerDiscordId))
                        .build())
                .build());

        World gpt354k = jpaRepository.save(WorldFixture.publicWorld()
                .id(null)
                .name("Number 3")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("world")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("world")
                .assetId(gpt4Mini.getId())
                .build();

        FavoriteEntity favorite3 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("world")
                .assetId(gpt354k.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2, favorite3));

        SearchWorlds query = SearchWorlds.builder()
                .requesterDiscordId(ownerDiscordId)
                .visibility("public")
                .favorites(true)
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);
    }

    @Test
    public void searcFavoritehWorlds_whenFilterByName_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        World gpt4Omni = jpaRepository.save(WorldFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(ownerDiscordId)
                        .build())
                .build());

        World gpt4Mini = jpaRepository.save(WorldFixture.publicWorld()
                .id(null)
                .name("Number 2")
                .permissions(PermissionsFixture.samplePermissions()
                        .usersAllowedToWrite(list(ownerDiscordId))
                        .build())
                .build());

        World gpt354k = jpaRepository.save(WorldFixture.publicWorld()
                .id(null)
                .name("Number 3")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("world")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("world")
                .assetId(gpt4Mini.getId())
                .build();

        FavoriteEntity favorite3 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("world")
                .assetId(gpt354k.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2, favorite3));

        SearchWorlds query = SearchWorlds.builder()
                .requesterDiscordId(ownerDiscordId)
                .name("Number 3")
                .favorites(true)
                .build();

        // When
        SearchWorldsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
    }
}
