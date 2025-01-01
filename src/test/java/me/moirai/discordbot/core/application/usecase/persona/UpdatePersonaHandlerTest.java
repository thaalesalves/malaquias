package me.moirai.discordbot.core.application.usecase.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.core.application.model.result.TextModerationResultFixture;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.application.usecase.persona.request.UpdatePersona;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaDomainRepository;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;
import me.moirai.discordbot.core.domain.persona.PersonaServiceImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class UpdatePersonaHandlerTest {

    @Mock
    private PersonaServiceImpl service;

    @Mock
    private PersonaDomainRepository repository;

    @Mock
    private TextModerationPort moderationPort;

    @InjectMocks
    private UpdatePersonaHandler handler;

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
                .requesterDiscordId("CRTID")
                .requesterDiscordId(requesterId)
                .usersAllowedToReadToAdd(Lists.list("123456"))
                .usersAllowedToWriteToAdd(Lists.list("123456"))
                .usersAllowedToReadToRemove(Lists.list("123456"))
                .usersAllowedToWriteToRemove(Lists.list("123456"))
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

        when(service.getById(anyString())).thenReturn(unchangedPersona);
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
                .requesterDiscordId("CRTID")
                .requesterDiscordId(requesterId)
                .usersAllowedToReadToAdd(Lists.list("123456"))
                .usersAllowedToWriteToAdd(null)
                .usersAllowedToReadToRemove(Lists.list("4567"))
                .usersAllowedToWriteToRemove(Lists.list("4567"))
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

        when(service.getById(anyString())).thenReturn(unchangedPersona);
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
                .requesterDiscordId("CRTID")
                .requesterDiscordId(requesterId)
                .usersAllowedToReadToAdd(null)
                .usersAllowedToWriteToAdd(Lists.list("123456"))
                .usersAllowedToReadToRemove(Lists.list("4567"))
                .usersAllowedToWriteToRemove(Lists.list("4567"))
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

        when(service.getById(anyString())).thenReturn(unchangedPersona);
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
                .requesterDiscordId("CRTID")
                .requesterDiscordId(requesterId)
                .usersAllowedToReadToAdd(Lists.list("123456"))
                .usersAllowedToWriteToAdd(Lists.list("123456"))
                .usersAllowedToReadToRemove(null)
                .usersAllowedToWriteToRemove(Lists.list("4567"))
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

        when(service.getById(anyString())).thenReturn(unchangedPersona);
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
                .requesterDiscordId("CRTID")
                .requesterDiscordId(requesterId)
                .usersAllowedToReadToAdd(Lists.list("123456"))
                .usersAllowedToWriteToAdd(Lists.list("123456"))
                .usersAllowedToReadToRemove(Lists.list("4567"))
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

        when(service.getById(anyString())).thenReturn(unchangedPersona);
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
                .requesterDiscordId(requesterId)
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

        when(service.getById(anyString())).thenReturn(unchangedPersona);
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
                .requesterDiscordId(requesterId)
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

        when(service.getById(anyString())).thenReturn(unchangedPersona);
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
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .name("MoirAI")
                .personality("I am a Discord chatbot")
                .visibility("PUBLIC")
                .requesterDiscordId("CRTID")
                .requesterDiscordId(requesterId)
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
                .requesterDiscordId(requesterId)
                .build();

        Persona unchangedPersona = PersonaFixture.privatePersona()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(service.getById(anyString())).thenReturn(unchangedPersona);
        when(repository.save(any(Persona.class))).thenReturn(unchangedPersona);

        // Then
        StepVerifier.create(handler.handle(command))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void updatePersona_whenNotEnoughPermissions_thenThrowException() {

        // Given
        String id = "PRSNID";

        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .requesterDiscordId("USRID")
                .build();

        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .build();

        when(service.getById(anyString())).thenReturn(persona);

        // Then
        StepVerifier.create(handler.handle(command))
                .verifyError(AssetAccessDeniedException.class);
    }
}
