package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import me.moirai.discordbot.AbstractDiscordTest;
import me.moirai.discordbot.core.application.helper.StoryGenerationHelper;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageDataFixture;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class GenerateOutputHandlerTest extends AbstractDiscordTest {

    @Mock
    private AdventureQueryRepository adventureRepository;

    @Mock
    private DiscordChannelPort discordChannelPort;

    @Mock
    private StoryGenerationHelper storyGenerationPort;

    @InjectMocks
    private GoCommandHandler handler;

    @Test
    public void goCommand_whenIssued_thenGenerateOutput() {

        // Given
        String channelId = "CHID";

        Adventure adventure = AdventureFixture.publicMultiplayerAdventure()
                .discordChannelId(channelId)
                .build();

        GoCommand useCase = GoCommand.builder()
                .botId("BOTID")
                .botNickname("nickname")
                .botUsername("user.name")
                .channelId(channelId)
                .guildId("GDID")
                .build();

        ArgumentCaptor<StoryGenerationRequest> generationRequestCaptor = ArgumentCaptor
                .forClass(StoryGenerationRequest.class);

        when(adventureRepository.findByDiscordChannelId(anyString())).thenReturn(Optional.of(adventure));

        when(discordChannelPort.getLastMessageIn(anyString()))
                .thenReturn(Optional.of(DiscordMessageDataFixture.messageData().build()));

        when(discordChannelPort.retrieveEntireHistoryBefore(anyString(), anyString()))
                .thenReturn(DiscordMessageDataFixture.messageList(5));

        // When
        Mono<Void> result = handler.execute(useCase);

        // Then
        StepVerifier.create(result).verifyComplete();

        verify(storyGenerationPort, times(1)).continueStory(generationRequestCaptor.capture());

        StoryGenerationRequest generationRequest = generationRequestCaptor.getValue();
        assertThat(generationRequest).isNotNull();
        assertThat(generationRequest.getBotId()).isEqualTo(useCase.getBotId());
        assertThat(generationRequest.getBotNickname()).isEqualTo(useCase.getBotNickname());
        assertThat(generationRequest.getBotUsername()).isEqualTo(useCase.getBotUsername());
        assertThat(generationRequest.getChannelId()).isEqualTo(useCase.getChannelId());
        assertThat(generationRequest.getGuildId()).isEqualTo(useCase.getGuildId());
        assertThat(generationRequest.getPersonaId()).isEqualTo(adventure.getPersonaId());
        assertThat(generationRequest.getAdventureId()).isEqualTo(adventure.getId());
    }

    @Test
    public void goCommand_whenUnknownError_thenThrowException() {

        // Given
        String channelId = "CHID";

        GoCommand useCase = GoCommand.builder()
                .botId("BOTID")
                .botNickname("nickname")
                .botUsername("user.name")
                .channelId(channelId)
                .guildId("GDID")
                .build();

        when(adventureRepository.findByDiscordChannelId(anyString())).thenThrow(RuntimeException.class);

        when(discordChannelPort.retrieveEntireHistoryFrom(anyString()))
                .thenReturn(DiscordMessageDataFixture.messageList(5));

        // When
        Mono<Void> result = handler.execute(useCase);

        // Then
        StepVerifier.create(result).verifyErrorMessage("An error occurred while generating output");
    }
}
