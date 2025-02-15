package me.moirai.discordbot.infrastructure.security.authorization.authorizer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Sets.set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResult;
import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResultFixture;
import me.moirai.discordbot.infrastructure.security.authentication.MoiraiPrincipal;
import me.moirai.discordbot.infrastructure.security.authentication.SecuritySessionContext;

@ExtendWith(MockitoExtension.class)
public class PersonaAuthorizerTest {

    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";

    @Mock
    private MoiraiPrincipal principal;

    @Mock
    private UseCaseRunner useCaseRunner;

    @InjectMocks
    private PersonaAuthorizer authorizer;

    @Test
    public void authorizePersona_whenGetAssetType_thenReturnCorrectType() {

        // Given
        String expectedAssetType = "Persona";

        // Then
        assertThat(authorizer.getAssetType()).isEqualTo(expectedAssetType);
    }

    @Test
    public void authorizePersona_whenCheckOwnership_andUserIsOwner_thenAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona()
                .ownerDiscordId(userId)
                .build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(USER);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.isOwner(personaId, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void authorizePersona_whenCheckOwnership_andUserIsAdmin_thenAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona().build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(ADMIN);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.isOwner(personaId, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void authorizePersona_whenCheckOwnership_andUserIsNotOwner_thenDontAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona().build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(USER);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.isOwner(personaId, userId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void authorizePersona_whenCheckModifyingRights_andUserIsOwner_thenAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona()
                .ownerDiscordId(userId)
                .build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(USER);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.canModify(personaId, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void authorizePersona_whenCheckModifyingRights_andUserIsAdmin_thenAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona().build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(ADMIN);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.canModify(personaId, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void authorizePersona_whenCheckModifyingRights_andUserIsWriter_thenAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona()
                .usersAllowedToWrite(set(userId))
                .build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(USER);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.canModify(personaId, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void authorizePersona_whenCheckModifyingRights_andUserIsReader_thenDontAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona()
                .usersAllowedToRead(set(userId))
                .build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(USER);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.canModify(personaId, userId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void authorizePersona_whenCheckReadingRights_andUserIsOwner_thenAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona()
                .ownerDiscordId(userId)
                .build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(USER);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.canRead(personaId, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void authorizePersona_whenCheckReadingRights_andUserIsAdmin_thenAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona().build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(ADMIN);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.canRead(personaId, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void authorizePersona_whenCheckReadingRights_andUserIsWriter_thenAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona()
                .usersAllowedToWrite(set(userId))
                .build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(USER);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.canRead(personaId, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void authorizePersona_whenCheckReadingRights_andUserIsReader_thenAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona()
                .usersAllowedToRead(set(userId))
                .build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(USER);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.canRead(personaId, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void authorizePersona_whenCheckReadingRights_andPersonaIsPublic_thenAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.publicPersona().build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(USER);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.canRead(personaId, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void authorizePersona_whenCheckReadingRights_andPersonaIsPrivate_andUserNotPermission_thenAuthorize() {

        // Given
        String personaId = "12345";
        String userId = "12345";
        GetPersonaResult personaDetails = GetPersonaResultFixture.privatePersona().build();

        SecuritySessionContext.setCurrentUser(principal);

        when(principal.getRole()).thenReturn(USER);
        when(useCaseRunner.run(any())).thenReturn(personaDetails);

        // When
        boolean result = authorizer.canRead(personaId, userId);

        // Then
        assertThat(result).isFalse();
    }
}
