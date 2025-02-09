package me.moirai.discordbot.core.application.usecase.persona;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.usecase.persona.request.DeletePersona;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaDomainRepository;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;

@ExtendWith(MockitoExtension.class)
public class DeletePersonaHandlerTest {

    @Mock
    private PersonaDomainRepository repository;

    @InjectMocks
    private DeletePersonaHandler handler;

    @Test
    public void deletePersona_whenIdIsNull_thenThrowException() {

        // Given
        String id = null;
        DeletePersona command = DeletePersona.build(id);

        // Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> handler.handle(command));
    }

    @Test
    public void deletePersona_whenPersonaNotFound_thenThrowException() {

        // Given
        String id = "PRSNID";
        DeletePersona command = DeletePersona.build(id);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThatExceptionOfType(AssetNotFoundException.class)
                .isThrownBy(() -> handler.handle(command));
    }

    @Test
    public void deletePersona_whenProperIdAndPermission_thenPersonaIsDeleted() {

        // Given
        String id = "PRSNID";
        String requesterId = "RQSTRID";
        DeletePersona command = DeletePersona.build(id);

        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // When
        handler.handle(command);

        // Then
        verify(repository, times(1)).deleteById(anyString());
    }
}
