package me.moirai.discordbot.core.application.usecase.world;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.usecase.world.request.AddFavoriteWorld;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.core.domain.world.WorldRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@ExtendWith(MockitoExtension.class)
public class AddFavoriteWorldHandlerTest {

    @Mock
    private WorldRepository worldRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private AddFavoriteWorldHandler handler;

    @Test
    public void addFavorite_whenValidAsset_thenCreateFavorite() {

        // Given
        AddFavoriteWorld command = AddFavoriteWorld.builder()
                .assetId("1234")
                .playerDiscordId("1234")
                .build();

        World world = WorldFixture.publicWorld().build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        // When
        handler.handle(command);

        // Then
        verify(favoriteRepository, times(1)).save(any());
    }

    @Test
    public void addFavorite_whenAssetNotFound_thenThrowException() {

        // Given
        AddFavoriteWorld command = AddFavoriteWorld.builder()
                .assetId("1234")
                .playerDiscordId("1234")
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void addFavorite_whenAccessDenied_thenThrowException() {

        // Given
        AddFavoriteWorld command = AddFavoriteWorld.builder()
                .assetId("1234")
                .playerDiscordId("INVLDUSR")
                .build();

        World world = WorldFixture.privateWorld().build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> handler.handle(command));
    }
}
