package me.moirai.discordbot.core.application.usecase.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureById;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;

@ExtendWith(MockitoExtension.class)
public class GetAdventureByIdHandlerTest {

    @Mock
    private AdventureQueryRepository queryRepository;

    @InjectMocks
    private GetAdventureByIdHandler handler;

    @Test
    public void errorWhenQueryIsNull() {

        // Given
        GetAdventureById query = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void getAdventureById() {

        // Given
        String id = "HAUDHUAHD";
        String requesterId = "RQSTRID";
        Adventure adventure = AdventureFixture.privateMultiplayerAdventure()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        GetAdventureById query = GetAdventureById.build(id, requesterId);

        when(queryRepository.findById(anyString())).thenReturn(Optional.of(adventure));

        // When
        GetAdventureResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getId()).isEqualTo(adventure.getId());
        assertThat(result.getAdventureStart()).isEqualTo(adventure.getAdventureStart());
        assertThat(result.getDescription()).isEqualTo(adventure.getDescription());
        assertThat(result.getDiscordChannelId()).isEqualTo(adventure.getDiscordChannelId());
        assertThat(result.getGameMode()).isEqualTo(adventure.getGameMode().name());
        assertThat(result.getName()).isEqualTo(adventure.getName());
        assertThat(result.getOwnerDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(result.getPersonaId()).isEqualTo(adventure.getPersonaId());
        assertThat(result.getVisibility()).isEqualTo(adventure.getVisibility().name());
        assertThat(result.getModeration()).isEqualTo(adventure.getModeration().name());
        assertThat(result.getWorldId()).isEqualTo(adventure.getWorldId());
        assertThat(result.isMultiplayer()).isEqualTo(adventure.isMultiplayer());
        assertThat(result.getCreationDate()).isNotNull();
        assertThat(result.getLastUpdateDate()).isNotNull();

        assertThat(result.getAuthorsNote()).isEqualTo(adventure.getContextAttributes().getAuthorsNote());
        assertThat(result.getNudge()).isEqualTo(adventure.getContextAttributes().getNudge());
        assertThat(result.getRemember()).isEqualTo(adventure.getContextAttributes().getRemember());
        assertThat(result.getBump()).isEqualTo(adventure.getContextAttributes().getBump());
        assertThat(result.getBumpFrequency()).isEqualTo(adventure.getContextAttributes().getBumpFrequency());

        assertThat(result.getAiModel()).isEqualToIgnoringCase(adventure.getModelConfiguration().getAiModel().toString());
        assertThat(result.getFrequencyPenalty()).isEqualTo(adventure.getModelConfiguration().getFrequencyPenalty());
        assertThat(result.getLogitBias()).isEqualTo(adventure.getModelConfiguration().getLogitBias());
        assertThat(result.getMaxTokenLimit()).isEqualTo(adventure.getModelConfiguration().getMaxTokenLimit());
        assertThat(result.getPresencePenalty()).isEqualTo(adventure.getModelConfiguration().getPresencePenalty());
        assertThat(result.getStopSequences()).isEqualTo(adventure.getModelConfiguration().getStopSequences());
        assertThat(result.getTemperature()).isEqualTo(adventure.getModelConfiguration().getTemperature());

        assertThat(result.getUsersAllowedToRead()).hasSameElementsAs(adventure.getUsersAllowedToRead());
        assertThat(result.getUsersAllowedToWrite()).hasSameElementsAs(adventure.getUsersAllowedToWrite());
    }

    @Test
    public void findAdventure_whenInvalidPermission_thenThrowException() {

        // Given
        String id = "ADVID";
        String requesterId = "RQSTRID";
        GetAdventureById query = GetAdventureById.build(id, requesterId);

        Adventure persona = AdventureFixture.privateMultiplayerAdventure()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .usersAllowedToRead(Collections.emptyList())
                        .build())
                .build();

        when(queryRepository.findById(anyString())).thenReturn(Optional.of(persona));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> handler.handle(query));
    }

    @Test
    public void findAdventure_whenAdventureNotFound_thenThrowException() {

        // Given
        String id = "ADVID";
        String requesterId = "RQSTRID";
        GetAdventureById query = GetAdventureById.build(id, requesterId);

        when(queryRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(query));
    }

    @Test
    public void findAdventure_whenValidId_thenAdventureIsReturned() {

        // Given
        String id = "ADVID";
        String requesterId = "RQSTRID";
        GetAdventureById query = GetAdventureById.build(id, requesterId);

        Adventure adventure = AdventureFixture.privateMultiplayerAdventure()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(queryRepository.findById(anyString())).thenReturn(Optional.of(adventure));

        // When
        GetAdventureResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(adventure.getName());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getId()).isEqualTo(adventure.getId());
        assertThat(result.getAdventureStart()).isEqualTo(adventure.getAdventureStart());
        assertThat(result.getDescription()).isEqualTo(adventure.getDescription());
        assertThat(result.getDiscordChannelId()).isEqualTo(adventure.getDiscordChannelId());
        assertThat(result.getGameMode()).isEqualTo(adventure.getGameMode().name());
        assertThat(result.getName()).isEqualTo(adventure.getName());
        assertThat(result.getOwnerDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(result.getPersonaId()).isEqualTo(adventure.getPersonaId());
        assertThat(result.getVisibility()).isEqualTo(adventure.getVisibility().name());
        assertThat(result.getModeration()).isEqualTo(adventure.getModeration().name());
        assertThat(result.getWorldId()).isEqualTo(adventure.getWorldId());
        assertThat(result.isMultiplayer()).isEqualTo(adventure.isMultiplayer());
        assertThat(result.getCreationDate()).isNotNull();
        assertThat(result.getLastUpdateDate()).isNotNull();

        assertThat(result.getAuthorsNote()).isEqualTo(adventure.getContextAttributes().getAuthorsNote());
        assertThat(result.getNudge()).isEqualTo(adventure.getContextAttributes().getNudge());
        assertThat(result.getRemember()).isEqualTo(adventure.getContextAttributes().getRemember());
        assertThat(result.getBump()).isEqualTo(adventure.getContextAttributes().getBump());
        assertThat(result.getBumpFrequency()).isEqualTo(adventure.getContextAttributes().getBumpFrequency());

        assertThat(result.getAiModel()).isEqualToIgnoringCase(adventure.getModelConfiguration().getAiModel().toString());
        assertThat(result.getFrequencyPenalty()).isEqualTo(adventure.getModelConfiguration().getFrequencyPenalty());
        assertThat(result.getLogitBias()).isEqualTo(adventure.getModelConfiguration().getLogitBias());
        assertThat(result.getMaxTokenLimit()).isEqualTo(adventure.getModelConfiguration().getMaxTokenLimit());
        assertThat(result.getPresencePenalty()).isEqualTo(adventure.getModelConfiguration().getPresencePenalty());
        assertThat(result.getStopSequences()).isEqualTo(adventure.getModelConfiguration().getStopSequences());
        assertThat(result.getTemperature()).isEqualTo(adventure.getModelConfiguration().getTemperature());

        assertThat(result.getUsersAllowedToRead()).hasSameElementsAs(adventure.getUsersAllowedToRead());
        assertThat(result.getUsersAllowedToWrite()).hasSameElementsAs(adventure.getUsersAllowedToWrite());
    }
}