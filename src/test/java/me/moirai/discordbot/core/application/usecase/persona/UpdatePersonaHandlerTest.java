package me.moirai.discordbot.core.application.usecase.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Sets.set;
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

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.core.application.model.result.TextModerationResultFixture;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.application.usecase.persona.request.UpdatePersona;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaDomainRepository;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class UpdatePersonaHandlerTest {

    @Mock
    private PersonaDomainRepository repository;

    @Mock
    private TextModerationPort moderationPort;

    @InjectMocks
    private UpdatePersonaHandler handler;

    @Test
    public void updatePersona_whenPersonaNotFound_thenThrowException() {

        // Given
        String id = "PRSNID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .name("MoirAI")
                .personality("I am a Discord chatbot")
                .visibility("PUBLIC")
                .usersAllowedToReadToAdd(set("123456"))
                .usersAllowedToWriteToAdd(set("123456"))
                .usersAllowedToReadToRemove(set("123456"))
                .usersAllowedToWriteToRemove(set("123456"))
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.empty());
        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        // Then
        StepVerifier.create(handler.handle(command))
                .verifyErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(AssetNotFoundException.class);
                    assertThat(error).message().isEqualTo("Persona was not found");
                });
    }

    @Test
    public void updatePersona_whenValidData_thenPersonaIsUpdated() {

        // Given
        String id = "PRSNID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .name("MoirAI")
                .personality("I am a Discord chatbot")
                .visibility("PUBLIC")
                .usersAllowedToReadToAdd(set("123456"))
                .usersAllowedToWriteToAdd(set("123456"))
                .usersAllowedToReadToRemove(set("123456"))
                .usersAllowedToWriteToRemove(set("123456"))
                .build();

        Persona unchangedPersona = PersonaFixture.privatePersona()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .name("New name")
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(expectedUpdatedPersona);
        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void updatePersona_whenNoWriterUsersAreAdded_thenPersonaIsUpdated() {

        // Given
        String id = "PRSNID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .name("MoirAI")
                .personality("I am a Discord chatbot")
                .visibility("PUBLIC")
                .usersAllowedToReadToAdd(set("123456"))
                .usersAllowedToWriteToAdd(null)
                .usersAllowedToReadToRemove(set("4567"))
                .usersAllowedToWriteToRemove(set("4567"))
                .build();

        Persona unchangedPersona = PersonaFixture.privatePersona()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .name("New name")
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(expectedUpdatedPersona);
        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void updatePersona_whenNoReaderUsersAreAdded_thenPersonaIsUpdated() {

        // Given
        String id = "PRSNID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .name("MoirAI")
                .personality("I am a Discord chatbot")
                .visibility("PUBLIC")
                .usersAllowedToReadToAdd(null)
                .usersAllowedToWriteToAdd(set("123456"))
                .usersAllowedToReadToRemove(set("4567"))
                .usersAllowedToWriteToRemove(set("4567"))
                .build();

        Persona unchangedPersona = PersonaFixture.privatePersona()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .name("New name")
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(expectedUpdatedPersona);
        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void updatePersona_whenNoReaderUsersAreRemoved_thenPersonaIsUpdated() {

        // Given
        String id = "PRSNID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .name("MoirAI")
                .personality("I am a Discord chatbot")
                .visibility("PUBLIC")
                .usersAllowedToReadToAdd(set("123456"))
                .usersAllowedToWriteToAdd(set("123456"))
                .usersAllowedToReadToRemove(null)
                .usersAllowedToWriteToRemove(set("4567"))
                .build();

        Persona unchangedPersona = PersonaFixture.privatePersona()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .name("New name")
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(expectedUpdatedPersona);
        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void updatePersona_whenNoWriterUsersAreRemoved_thenPersonaIsUpdated() {

        // Given
        String id = "PRSNID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .name("MoirAI")
                .personality("I am a Discord chatbot")
                .visibility("PUBLIC")
                .usersAllowedToReadToAdd(set("123456"))
                .usersAllowedToWriteToAdd(set("123456"))
                .usersAllowedToReadToRemove(set("4567"))
                .usersAllowedToWriteToRemove(null)
                .build();

        Persona unchangedPersona = PersonaFixture.privatePersona()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .name("New name")
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(expectedUpdatedPersona);
        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withoutFlags().build()));

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void updatePersona_whenPublicToMakePrivate_thenPersonaIsMadePrivate() {

        // Given
        String id = "PRSNID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .visibility("private")
                .build();

        Persona unchangedPersona = PersonaFixture.publicPersona()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona()
                .id(id)
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(expectedUpdatedPersona);

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void updatePersona_whenInvalidVisibility_thenNothingIsChanged() {

        // Given
        String id = "PRSNID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .visibility("invalid")
                .build();

        Persona unchangedPersona = PersonaFixture.privatePersona()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona()
                .id(id)
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(expectedUpdatedPersona);

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void updatePersona_whenContentIsFlagged_thenThrowException() {

        // Given
        String id = "PRSNID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .name("MoirAI")
                .personality("I am a Discord chatbot")
                .visibility("PUBLIC")
                .build();

        when(moderationPort.moderate(anyString()))
                .thenReturn(Mono.just(TextModerationResultFixture.withFlags().build()));

        // Then
        StepVerifier.create(handler.handle(command))
                .verifyError(ModerationException.class);
    }

    @Test
    public void updatePersona_whenUpdateFieldsAreEmpty_thenPersonaIsNotChanged() {

        // Given
        String id = "PRSNID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .build();

        Persona unchangedPersona = PersonaFixture.privatePersona()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(unchangedPersona);

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }
}
