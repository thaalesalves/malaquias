package me.moirai.discordbot.core.application.usecase.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.port.WorldQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.CreateAdventure;
import me.moirai.discordbot.core.application.usecase.adventure.request.CreateAdventureFixture;
import me.moirai.discordbot.core.application.usecase.adventure.result.CreateAdventureResult;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureRepository;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntryRepository;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryFixture;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryRepository;

@ExtendWith(MockitoExtension.class)
public class CreateAdventureHandlerTest {

    @Mock
    private WorldLorebookEntryRepository worldLorebookEntryRepository;

    @Mock
    private WorldQueryRepository worldQueryRepository;

    @Mock
    private PersonaQueryRepository personaQueryRepository;

    @Mock
    private AdventureRepository repository;

    @Mock
    private AdventureLorebookEntryRepository lorebookEntryRepository;

    @InjectMocks
    private CreateAdventureHandler handler;

    @Test
    public void createAdventure_whenWorldNotFound_thenThrowException() {

        // Given
        CreateAdventure command = CreateAdventureFixture.sample().build();

        when(worldQueryRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void createAdventure_whenNoWorldPermission_thenThrowException() {

        // Given
        String userId = "someUserId";
        CreateAdventure command = CreateAdventureFixture.sample()
                .requesterDiscordId(userId)
                .build();

        World world = WorldFixture.privateWorld().build();

        when(worldQueryRepository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> handler.handle(command));
    }

    @Test
    public void createAdventure_whenPersonaNotFound_thenThrowException() {

        // Given
        String userId = "someUserId";
        CreateAdventure command = CreateAdventureFixture.sample()
                .requesterDiscordId(userId)
                .build();

        World world = WorldFixture.privateWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(userId)
                        .build())
                .build();

        when(worldQueryRepository.findById(anyString())).thenReturn(Optional.of(world));
        when(personaQueryRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void createAdventure_whenNoPersonaPermission_thenThrowException() {

        // Given
        String userId = "someUserId";
        CreateAdventure command = CreateAdventureFixture.sample()
                .requesterDiscordId(userId)
                .build();

        World world = WorldFixture.privateWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(userId)
                        .build())
                .build();

        Persona persona = PersonaFixture.privatePersona().build();

        when(worldQueryRepository.findById(anyString())).thenReturn(Optional.of(world));
        when(personaQueryRepository.findById(anyString())).thenReturn(Optional.of(persona));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> handler.handle(command));
    }

    @Test
    public void createAdventure_whenValidDate_thenAdventureIsCreated() {

        // Given
        String id = "HAUDHUAHD";
        CreateAdventure command = CreateAdventureFixture.sample().build();
        Adventure adventure = AdventureFixture.privateMultiplayerAdventure()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(command.getRequesterDiscordId())
                        .build())
                .build();

        World world = WorldFixture.privateWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(command.getRequesterDiscordId())
                        .build())
                .build();

        Persona persona = PersonaFixture.privatePersona()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(command.getRequesterDiscordId())
                        .build())
                .build();


        when(worldQueryRepository.findById(anyString())).thenReturn(Optional.of(world));
        when(personaQueryRepository.findById(anyString())).thenReturn(Optional.of(persona));
        when(repository.save(any(Adventure.class))).thenReturn(adventure);
        when(worldLorebookEntryRepository.findAllByWorldId(anyString()))
                .thenReturn(list(WorldLorebookEntryFixture.sampleLorebookEntry().build()));

        // When
        CreateAdventureResult result = handler.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}
