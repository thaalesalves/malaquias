package me.moirai.discordbot.core.application.usecase.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventure;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureFixture;
import me.moirai.discordbot.core.application.usecase.adventure.result.UpdateAdventureResult;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;

@ExtendWith(MockitoExtension.class)
public class UpdateAdventureHandlerTest {

    @Mock
    private AdventureDomainRepository repository;

    @InjectMocks
    private UpdateAdventureHandler handler;

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;
        UpdateAdventure command = UpdateAdventureFixture.sample()
                .id(id)
                .build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void updateAdventure() {

        // Given
        String id = "ADVID";
        String requesterId = "DASDASD";
        UpdateAdventure command = UpdateAdventureFixture.sample()
                .id(id)
                .name("Name")
                .worldId("WRLDID")
                .personaId("PRSNID")
                .moderation("STRICT")
                .visibility("PRIVATE")
                .requesterDiscordId(requesterId)
                .build();

        Adventure expectedUpdatedAdventure = AdventureFixture.privateMultiplayerAdventure()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(expectedUpdatedAdventure));
        when(repository.save(any())).thenReturn(expectedUpdatedAdventure);

        // When
        UpdateAdventureResult result = handler.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLastUpdatedDateTime()).isEqualTo(expectedUpdatedAdventure.getLastUpdateDate());
    }

    @Test
    public void updateAdventure_whenAdventureToUpdateNotFound_thenThrowException() {

        // Given
        String requesterUserId = "LALALA";
        UpdateAdventure updateAdventure = UpdateAdventureFixture.sample()
                .requesterDiscordId(requesterUserId)
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.execute(updateAdventure));
    }

    @Test
    public void updateAdventure_whenPrivateToBeMadePublic_thenAdventureIsMadePublic() {

        // Given
        String id = "ADVID";
        String requesterId = "RQSTRID";
        UpdateAdventure command = UpdateAdventureFixture.sample()
                .requesterDiscordId(requesterId)
                .visibility("public")
                .build();

        Adventure unchangedAdventure = AdventureFixture.privateMultiplayerAdventure()
                .visibility(Visibility.PRIVATE)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Adventure expectedUpdatedAdventure = AdventureFixture.privateMultiplayerAdventure()
                .id(id)
                .visibility(Visibility.PUBLIC)
                .build();

        ArgumentCaptor<Adventure> adventureCaptor = ArgumentCaptor.forClass(Adventure.class);

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedAdventure));
        when(repository.save(adventureCaptor.capture())).thenReturn(expectedUpdatedAdventure);

        // When
        handler.execute(command);

        // Then
        Adventure capturedAdventure = adventureCaptor.getValue();
        assertThat(capturedAdventure.getVisibility()).isEqualTo(unchangedAdventure.getVisibility());
    }

    @Test
    public void updateAdventure_whenInvalidVisibility_thenNothingIsChanged() {

        // Given
        String id = "ADVID";
        String requesterId = "RQSTRID";
        UpdateAdventure command = UpdateAdventureFixture.sample()
                .requesterDiscordId(requesterId)
                .visibility("invalid")
                .build();

        Adventure unchangedAdventure = AdventureFixture.privateMultiplayerAdventure()
                .visibility(Visibility.PRIVATE)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Adventure expectedUpdatedAdventure = AdventureFixture.privateMultiplayerAdventure()
                .id(id)
                .visibility(Visibility.PRIVATE)
                .build();

        ArgumentCaptor<Adventure> adventureCaptor = ArgumentCaptor.forClass(Adventure.class);

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedAdventure));
        when(repository.save(adventureCaptor.capture())).thenReturn(expectedUpdatedAdventure);

        // When
        handler.execute(command);

        // Then
        Adventure capturedAdventure = adventureCaptor.getValue();
        assertThat(capturedAdventure.getName()).isEqualTo(unchangedAdventure.getName());
    }

    @Test
    public void updateAdventure_whenEmptyUpdateFields_thenNothingIsChanged() {

        // Given
        String id = "ADVID";
        String requesterId = "RQSTRID";
        UpdateAdventure command = UpdateAdventureFixture.sample()
                .requesterDiscordId(requesterId)
                .id(id)
                .aiModel(null)
                .discordChannelId(null)
                .frequencyPenalty(null)
                .logitBiasToAdd(null)
                .logitBiasToRemove(null)
                .maxTokenLimit(null)
                .moderation(null)
                .name(null)
                .personaId(null)
                .presencePenalty(null)
                .stopSequencesToAdd(null)
                .stopSequencesToRemove(null)
                .usersAllowedToReadToAdd(null)
                .usersAllowedToReadToRemove(null)
                .usersAllowedToWriteToAdd(null)
                .usersAllowedToWriteToRemove(null)
                .temperature(null)
                .visibility(null)
                .worldId(null)
                .adventureStart(null)
                .description(null)
                .gameMode(null)
                .authorsNote(null)
                .nudge(null)
                .remember(null)
                .bump(null)
                .build();

        Adventure unchangedAdventure = AdventureFixture.privateMultiplayerAdventure()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        ArgumentCaptor<Adventure> adventureCaptor = ArgumentCaptor.forClass(Adventure.class);

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedAdventure));
        when(repository.save(adventureCaptor.capture())).thenReturn(unchangedAdventure);

        // When
        handler.execute(command);

        // Then
        Adventure capturedAdventure = adventureCaptor.getValue();
        assertThat(capturedAdventure.getName()).isEqualTo(unchangedAdventure.getName());
    }

    @Test
    public void updateAdventure_whenAdventureIsSingleplayer_thenUpdateToMultiplayer() {

        // Given
        String requesterId = "RQSTRID";
        UpdateAdventure command = UpdateAdventureFixture.sample()
                .isMultiplayer(true)
                .requesterDiscordId(requesterId)
                .build();

        Adventure adventure = AdventureFixture.privateMultiplayerAdventure()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        ArgumentCaptor<Adventure> adventureCaptor = ArgumentCaptor.forClass(Adventure.class);

        when(repository.findById(anyString())).thenReturn(Optional.of(adventure));
        when(repository.save(adventureCaptor.capture())).thenReturn(adventure);

        // When
        handler.execute(command);

        // Then
        Adventure capturedAdventure = adventureCaptor.getValue();
        assertThat(capturedAdventure.isMultiplayer()).isTrue();
    }

    @Test
    public void updateAdventure_whenAdventureIsMultiplayer_thenUpdateToSingleplayer() {

        // Given
        String requesterId = "RQSTRID";
        UpdateAdventure command = UpdateAdventureFixture.sample()
                .isMultiplayer(false)
                .requesterDiscordId(requesterId)
                .build();

        Adventure adventure = AdventureFixture.privateMultiplayerAdventure()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        ArgumentCaptor<Adventure> adventureCaptor = ArgumentCaptor.forClass(Adventure.class);

        when(repository.findById(anyString())).thenReturn(Optional.of(adventure));
        when(repository.save(adventureCaptor.capture())).thenReturn(adventure);

        // When
        handler.execute(command);

        // Then
        Adventure capturedAdventure = adventureCaptor.getValue();
        assertThat(capturedAdventure.isMultiplayer()).isFalse();
    }
}
