package me.moirai.discordbot.core.application.usecase.completion;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AIModelNotSupportedException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.core.application.helper.LorebookEnrichmentHelper;
import me.moirai.discordbot.core.application.model.result.TextGenerationResult;
import me.moirai.discordbot.core.application.model.result.TextGenerationResultFixture;
import me.moirai.discordbot.core.application.model.result.TextModerationResult;
import me.moirai.discordbot.core.application.model.result.TextModerationResultFixture;
import me.moirai.discordbot.core.application.port.DiscordUserDetailsPort;
import me.moirai.discordbot.core.application.port.TextCompletionPort;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.application.usecase.completion.request.CompleteText;
import me.moirai.discordbot.core.application.usecase.completion.request.CompleteTextFixture;
import me.moirai.discordbot.core.application.usecase.completion.result.CompleteTextResult;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetailsFixture;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeResult;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeResultFixture;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;
import me.moirai.discordbot.core.domain.persona.PersonaService;
import me.moirai.discordbot.core.domain.port.TokenizerPort;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.core.domain.world.WorldService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class CompleteTextHandlerTest {

    @Mock
    private PersonaService personaService;

    @Mock
    private WorldService worldService;

    @Mock
    private LorebookEnrichmentHelper lorebookEnrichmentHelper;

    @Mock
    private TextModerationPort textModerationPort;

    @Mock
    private TextCompletionPort textCompletionPort;

    @Mock
    private DiscordUserDetailsPort discordUserDetailsPort;

    @Mock
    private TokenizerPort tokenizerPort;

    @InjectMocks
    private CompleteTextHandler handler;

    @Test
    public void whenNoFlaggedContent_andModerationIsDisabled_thenResultIsReturned() {

        // Given
        CompleteText command = CompleteTextFixture.withModerationDisabled().build();
        Persona persona = PersonaFixture.privatePersona().build();
        World world = WorldFixture.privateWorld().build();
        DiscordUserDetails userDetails = DiscordUserDetailsFixture.create().build();
        TextGenerationResult textGenerationResult = TextGenerationResultFixture.create().build();
        TokenizeResult tokenizeResult = TokenizeResultFixture.create().build();
        TextModerationResult textModerationResult = TextModerationResultFixture.withoutFlags().build();

        when(personaService.getById(anyString())).thenReturn(persona);
        when(worldService.getWorldById(anyString())).thenReturn(world);
        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.of(userDetails));
        when(textCompletionPort.generateTextFrom(any())).thenReturn(Mono.just(textGenerationResult));
        when(tokenizerPort.tokenize(anyString())).thenReturn(tokenizeResult);
        when(textModerationPort.moderate(anyString())).thenReturn(Mono.just(textModerationResult));

        // When
        Mono<CompleteTextResult> result = handler.handle(command);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void whenFlaggedContent_andModerationIsDisabled_thenResultIsReturned() {

        // Given
        CompleteText command = CompleteTextFixture.withModerationDisabled().build();
        Persona persona = PersonaFixture.privatePersona().build();
        World world = WorldFixture.privateWorld().build();
        DiscordUserDetails userDetails = DiscordUserDetailsFixture.create().build();
        TextGenerationResult textGenerationResult = TextGenerationResultFixture.create().build();
        TokenizeResult tokenizeResult = TokenizeResultFixture.create().build();
        TextModerationResult textModerationResult = TextModerationResultFixture.withFlags().build();

        when(personaService.getById(anyString())).thenReturn(persona);
        when(worldService.getWorldById(anyString())).thenReturn(world);
        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.of(userDetails));
        when(textCompletionPort.generateTextFrom(any())).thenReturn(Mono.just(textGenerationResult));
        when(tokenizerPort.tokenize(anyString())).thenReturn(tokenizeResult);
        when(textModerationPort.moderate(anyString())).thenReturn(Mono.just(textModerationResult));

        // When
        Mono<CompleteTextResult> result = handler.handle(command);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void whenNoFlaggedContent_andModerationIsStrict_thenResultIsReturned() {

        // Given
        CompleteText command = CompleteTextFixture.withStrictModeration().build();
        Persona persona = PersonaFixture.privatePersona().build();
        World world = WorldFixture.privateWorld().build();
        DiscordUserDetails userDetails = DiscordUserDetailsFixture.create().build();
        TextGenerationResult textGenerationResult = TextGenerationResultFixture.create().build();
        TokenizeResult tokenizeResult = TokenizeResultFixture.create().build();
        TextModerationResult textModerationResult = TextModerationResultFixture.withoutFlags().build();

        when(personaService.getById(anyString())).thenReturn(persona);
        when(worldService.getWorldById(anyString())).thenReturn(world);
        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.of(userDetails));
        when(textCompletionPort.generateTextFrom(any())).thenReturn(Mono.just(textGenerationResult));
        when(tokenizerPort.tokenize(anyString())).thenReturn(tokenizeResult);
        when(textModerationPort.moderate(anyString())).thenReturn(Mono.just(textModerationResult));

        // When
        Mono<CompleteTextResult> result = handler.handle(command);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void whenNoFlaggedContent_andModerationIsPermissive_thenResultIsReturned() {

        // Given
        CompleteText command = CompleteTextFixture.withPermissiveModeration().build();
        Persona persona = PersonaFixture.privatePersona().build();
        World world = WorldFixture.privateWorld().build();
        DiscordUserDetails userDetails = DiscordUserDetailsFixture.create().build();
        TextGenerationResult textGenerationResult = TextGenerationResultFixture.create().build();
        TokenizeResult tokenizeResult = TokenizeResultFixture.create().build();
        TextModerationResult textModerationResult = TextModerationResultFixture.withoutFlags().build();

        when(personaService.getById(anyString())).thenReturn(persona);
        when(worldService.getWorldById(anyString())).thenReturn(world);
        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.of(userDetails));
        when(textCompletionPort.generateTextFrom(any())).thenReturn(Mono.just(textGenerationResult));
        when(tokenizerPort.tokenize(anyString())).thenReturn(tokenizeResult);
        when(textModerationPort.moderate(anyString())).thenReturn(Mono.just(textModerationResult));

        // When
        Mono<CompleteTextResult> result = handler.handle(command);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void whenFlaggedContent_andModerationIsStrict_thenExceptionIsThrown() {

        // Given
        CompleteText command = CompleteTextFixture.withStrictModeration().build();
        Persona persona = PersonaFixture.privatePersona().build();
        World world = WorldFixture.privateWorld().build();
        DiscordUserDetails userDetails = DiscordUserDetailsFixture.create().build();
        TextModerationResult textModerationResult = TextModerationResultFixture.withFlags().build();

        when(personaService.getById(anyString())).thenReturn(persona);
        when(worldService.getWorldById(anyString())).thenReturn(world);
        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.of(userDetails));
        when(textModerationPort.moderate(anyString())).thenReturn(Mono.just(textModerationResult));

        // When
        Mono<CompleteTextResult> result = handler.handle(command);

        // Then
        StepVerifier.create(result)
                .verifyError(ModerationException.class);
    }

    @Test
    public void whenFlaggedContentInInput_andModerationIsPermissive_thenExceptionIsThrown() {

        // Given
        CompleteText command = CompleteTextFixture.withPermissiveModeration().build();
        Persona persona = PersonaFixture.privatePersona().build();
        World world = WorldFixture.privateWorld().build();
        DiscordUserDetails userDetails = DiscordUserDetailsFixture.create().build();
        TextModerationResult textModerationResult = TextModerationResultFixture.withFlags().build();

        when(personaService.getById(anyString())).thenReturn(persona);
        when(worldService.getWorldById(anyString())).thenReturn(world);
        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.of(userDetails));
        when(textModerationPort.moderate(anyString())).thenReturn(Mono.just(textModerationResult));

        // When
        Mono<CompleteTextResult> result = handler.handle(command);

        // Then
        StepVerifier.create(result)
                .verifyError(ModerationException.class);
    }

    @Test
    public void whenFlaggedContentInOutput_andModerationIsPermissive_thenExceptionIsThrown() {

        // Given
        CompleteText command = CompleteTextFixture.withPermissiveModeration().build();
        Persona persona = PersonaFixture.privatePersona().build();
        World world = WorldFixture.privateWorld().build();
        DiscordUserDetails userDetails = DiscordUserDetailsFixture.create().build();
        TextGenerationResult textGenerationResult = TextGenerationResultFixture.create().build();
        TextModerationResult badModerationResult = TextModerationResultFixture.withFlags().build();
        TextModerationResult goodModerationResult = TextModerationResultFixture.withoutFlags().build();

        when(personaService.getById(anyString())).thenReturn(persona);
        when(worldService.getWorldById(anyString())).thenReturn(world);
        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.of(userDetails));
        when(textCompletionPort.generateTextFrom(any())).thenReturn(Mono.just(textGenerationResult));
        when(textModerationPort.moderate(anyString()))
                .thenReturn(Mono.just(goodModerationResult))
                .thenReturn(Mono.just(badModerationResult));

        // When
        Mono<CompleteTextResult> result = handler.handle(command);

        // Then
        StepVerifier.create(result)
                .verifyError(ModerationException.class);
    }

    @Test
    public void whenInvalidModel_thenErrorIsThrown() {

        // Given
        CompleteText command = CompleteTextFixture.withPermissiveModeration()
                .aiModel("invalidModel")
                .build();

        // Then
        assertThrows(AIModelNotSupportedException.class,
                () -> handler.handle(command));
    }
}
