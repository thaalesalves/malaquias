package me.moirai.discordbot.core.application.usecase.persona;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.core.application.usecase.persona.request.DeletePersona;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;
import me.moirai.discordbot.core.domain.persona.PersonaService;

@ExtendWith(MockitoExtension.class)
public class DeletePersonaHandlerTest {

    @Mock
    private PersonaService domainService;

    @InjectMocks
    private DeletePersonaHandler handler;

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;
        String requesterId = "RUEYAHA";

        DeletePersona config = DeletePersona.build(id, requesterId);

        // Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> handler.handle(config));
    }

    @Test
    public void deletePersona_whenProperIdAndPermission_thenPersonaIsDeleted() {

        // Given
        String id = "PRSNID";
        String requesterId = "RQSTRID";
        DeletePersona command = DeletePersona.build(id, requesterId);

        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(domainService.getById(anyString())).thenReturn(persona);

        // When
        handler.handle(command);

        // Then
        verify(domainService, times(1)).delete(any());
    }

    @Test
    public void deletePersona_whenInvalidPermission_thenThrowException() {

        // Given
        String id = "PRSNID";
        String requesterId = "RQSTRID";
        DeletePersona command = DeletePersona.build(id, requesterId);

        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .build())
                .build();

        when(domainService.getById(anyString())).thenReturn(persona);

        // Then
        assertThatExceptionOfType(AssetAccessDeniedException.class)
                .isThrownBy(() -> handler.handle(command));
    }
}
