package me.moirai.discordbot.core.application.usecase.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldById;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldResult;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.core.domain.world.WorldRepository;

@ExtendWith(MockitoExtension.class)
public class GetWorldByIdHandlerTest {

    @Mock
    private WorldRepository repository;

    @InjectMocks
    private GetWorldByIdHandler handler;

    @Test
    public void errorWhenQueryIsNull() {

        // Given
        GetWorldById query = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void getWorldById() {

        // Given
        String id = "HAUDHUAHD";
        World world = WorldFixture.privateWorld().id(id).build();
        GetWorldById query = GetWorldById.build(id);

        when(repository.findById(anyString())).thenReturn(Optional.of(world));

        // When
        GetWorldResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void updateWorld_whenIdIsNull_thenExceptionIsThrown() {

        // Given
        String id = null;
        GetWorldById command = GetWorldById.build(id);

        // Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> handler.handle(command));
    }

    @Test
    public void updateWorld_whenWorldNotFound_thenExceptionIsThrown() {

        // Given
        String id = "WRLDID";
        GetWorldById command = GetWorldById.build(id);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThatExceptionOfType(AssetNotFoundException.class)
                .isThrownBy(() -> handler.handle(command));
    }
}
