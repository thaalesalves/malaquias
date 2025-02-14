package me.moirai.discordbot.core.application.usecase.world;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorld;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldRepository;
import me.moirai.discordbot.core.domain.world.WorldFixture;

@ExtendWith(MockitoExtension.class)
public class DeleteWorldHandlerTest {

    @Mock
    private WorldRepository domainRepository;

    @InjectMocks
    private DeleteWorldHandler handler;

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;
        DeleteWorld config = DeleteWorld.build(id);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(config));
    }

    @Test
    public void deleteWorld() {

        // Given
        String requesterDiscordId = "84REAC";
        String id = "WRDID";

        World world = WorldFixture.publicWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterDiscordId)
                        .usersAllowedToRead(Collections.emptySet())
                        .build())
                .build();

        DeleteWorld command = DeleteWorld.build(id);

        when(domainRepository.findById(anyString())).thenReturn(Optional.of(world));
        doNothing().when(domainRepository).deleteById(anyString());

        // When
        handler.handle(command);

        // Then
        verify(domainRepository, times(1)).deleteById(anyString());
    }

    @Test
    public void updateWorld_whenWorldNotFound_thenExceptionIsThrown() {

        // Given
        String id = "WRLDID";
        DeleteWorld command = DeleteWorld.build(id);

        when(domainRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThatExceptionOfType(AssetNotFoundException.class)
                .isThrownBy(() -> handler.handle(command));
    }
}
