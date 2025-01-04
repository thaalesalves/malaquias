package me.moirai.discordbot.core.application.usecase.completion;

import static java.util.Collections.emptyList;
import static me.moirai.discordbot.core.application.model.request.ChatMessage.Role.ASSISTANT;
import static me.moirai.discordbot.core.application.model.request.ChatMessage.Role.SYSTEM;
import static me.moirai.discordbot.core.application.model.request.ChatMessage.Role.USER;
import static me.moirai.discordbot.core.domain.adventure.ArtificialIntelligenceModel.fromString;
import static me.moirai.discordbot.core.domain.adventure.Moderation.DISABLED;
import static org.apache.commons.collections4.MapUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.micrometer.common.util.StringUtils;
import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.helper.LorebookEnrichmentHelper;
import me.moirai.discordbot.core.application.model.request.ChatMessage;
import me.moirai.discordbot.core.application.model.request.TextGenerationRequest;
import me.moirai.discordbot.core.application.model.result.TextGenerationResult;
import me.moirai.discordbot.core.application.port.DiscordUserDetailsPort;
import me.moirai.discordbot.core.application.port.TextCompletionPort;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.application.usecase.completion.request.CompleteText;
import me.moirai.discordbot.core.application.usecase.completion.request.CompleteText.Message;
import me.moirai.discordbot.core.application.usecase.completion.result.CompleteTextResult;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeResult;
import me.moirai.discordbot.core.domain.adventure.ArtificialIntelligenceModel;
import me.moirai.discordbot.core.domain.adventure.Moderation;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaService;
import me.moirai.discordbot.core.domain.port.TokenizerPort;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldService;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.AiModelRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModerationConfigurationRequest;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class CompleteTextHandler extends AbstractUseCaseHandler<CompleteText, Mono<CompleteTextResult>> {

    private final PersonaService personaService;
    private final WorldService worldService;
    private final LorebookEnrichmentHelper lorebookEnrichmentHelper;
    private final TextModerationPort textModerationPort;
    private final TextCompletionPort textCompletionPort;
    private final DiscordUserDetailsPort discordUserDetailsPort;
    private final TokenizerPort tokenizerPort;

    public CompleteTextHandler(
            PersonaService personaService,
            WorldService worldService,
            LorebookEnrichmentHelper lorebookEnrichmentHelper,
            TextModerationPort textModerationPort,
            TextCompletionPort textCompletionPort,
            DiscordUserDetailsPort discordUserDetailsPort,
            TokenizerPort tokenizerPort) {

        this.personaService = personaService;
        this.worldService = worldService;
        this.lorebookEnrichmentHelper = lorebookEnrichmentHelper;
        this.textModerationPort = textModerationPort;
        this.textCompletionPort = textCompletionPort;
        this.discordUserDetailsPort = discordUserDetailsPort;
        this.tokenizerPort = tokenizerPort;
    }

    @Override
    public Mono<CompleteTextResult> execute(CompleteText useCase) {

        ArtificialIntelligenceModel model = fromString(useCase.getAiModel());
        Persona persona = personaService.getById(useCase.getPersonaId());
        World world = worldService.getWorldById(useCase.getWorldId());
        DiscordUserDetails author = discordUserDetailsPort.getUserById(useCase.getAuthorDiscordId())
                .orElseThrow(() -> new AssetNotFoundException("User not found"));

        List<ChatMessage> context = useCase.getMessages().stream()
                .map(this::mapMessage)
                .collect(Collectors.toCollection(ArrayList::new));

        ChatMessage personality = ChatMessage.build(SYSTEM, persona.getPersonality());
        ChatMessage lorebook = extractLorebookEntriesFromHistory(useCase, world, author, model);

        if (StringUtils.isNotBlank(lorebook.getContent())) {
            context.addFirst(lorebook);
        }

        context.addFirst(personality);

        return generateAiOutput(useCase, context, model)
                .map(textGeneration -> {
                    TokenizeResult tokenizeResult = tokenizerPort.tokenize(textGeneration.getOutputText());
                    return CompleteTextResult.builder()
                            .outputText(textGeneration.getOutputText())
                            .completionTokens(textGeneration.getCompletionTokens())
                            .promptTokens(textGeneration.getPromptTokens())
                            .totalTokens(textGeneration.getTotalTokens())
                            .completionTokens(textGeneration.getCompletionTokens())
                            .tokenIds(tokenizeResult.getTokenIds())
                            .tokens(tokenizeResult.getTokens().split("\\|"))
                            .build();
                });
    }

    private ChatMessage mapMessage(Message message) {
        return ChatMessage.build(message.isAuthorBot() ? ASSISTANT : USER, message.getMessageContent());
    }

    private ChatMessage extractLorebookEntriesFromHistory(CompleteText useCase, World world,
            DiscordUserDetails author, ArtificialIntelligenceModel model) {

        ModelConfigurationRequest modelConfigRequest = ModelConfigurationRequest.builder()
                .frequencyPenalty(useCase.getFrequencyPenalty())
                .presencePenalty(useCase.getPresencePenalty())
                .logitBias(useCase.getLogitBias())
                .maxTokenLimit(useCase.getMaxTokenLimit())
                .stopSequences(useCase.getStopSequences())
                .temperature(useCase.getTemperature())
                .aiModel(AiModelRequest.build(model.toString(),
                        model.getOfficialModelName(), model.getHardTokenLimit()))
                .build();

        List<DiscordMessageData> messageHistory = useCase.getMessages().stream()
                .map(message -> DiscordMessageData.builder()
                        .content(message.getMessageContent())
                        .author(message.isAuthorBot() ? discordUserDetailsPort.getBotUser() : author)
                        .build())
                .toList();

        Map<String, Object> context = lorebookEnrichmentHelper.enrichContextWithLorebook(
                messageHistory, world.getId(), modelConfigRequest);

        String lorebook = (String) context.get("lorebook");

        return ChatMessage.build(SYSTEM, lorebook);
    }

    private Mono<TextGenerationResult> generateAiOutput(
            CompleteText useCase, List<ChatMessage> messageHistory, ArtificialIntelligenceModel model) {

        TextGenerationRequest textGenerationRequest = buildTextGenerationRequest(useCase, messageHistory, model);

        Moderation moderation = Moderation.fromString(useCase.getModerationLevel());
        boolean isModerationEnabled = !moderation.equals(DISABLED);
        ModerationConfigurationRequest moderationRequest = ModerationConfigurationRequest.build(
                isModerationEnabled, moderation.isAbsolute(), moderation.getThresholds());

        return moderateInput(messageHistory, moderationRequest)
                .flatMap(moderationResponse -> textCompletionPort.generateTextFrom(textGenerationRequest))
                .flatMap(generationResponse -> moderateOutput(generationResponse, moderationRequest)
                        .map(moderationResponse -> generationResponse));
    }

    private TextGenerationRequest buildTextGenerationRequest(
            CompleteText useCase, List<ChatMessage> messageHistory, ArtificialIntelligenceModel model) {

        return TextGenerationRequest.builder()
                .frequencyPenalty(useCase.getFrequencyPenalty())
                .presencePenalty(useCase.getPresencePenalty())
                .temperature(useCase.getTemperature())
                .model(model.getOfficialModelName())
                .logitBias(useCase.getLogitBias())
                .maxTokens(useCase.getMaxTokenLimit())
                .stopSequences(useCase.getStopSequences())
                .messages(messageHistory)
                .build();
    }

    private Mono<List<ChatMessage>> moderateInput(List<ChatMessage> messages,
            ModerationConfigurationRequest moderation) {

        String messageHistory = messages.stream()
                .map(ChatMessage::getContent)
                .collect(Collectors.joining("\n"));

        return getTopicsFlaggedByModeration(messageHistory, moderation)
                .map(result -> {
                    if (!result.isEmpty()) {
                        throw new ModerationException("Inappropriate content found in message history", result);
                    }

                    return messages;
                });
    }

    private Mono<String> moderateOutput(TextGenerationResult generationResult,
            ModerationConfigurationRequest moderation) {

        return getTopicsFlaggedByModeration(generationResult.getOutputText(), moderation)
                .map(result -> {
                    if (!result.isEmpty()) {
                        throw new ModerationException("Inappropriate content found in AI's output", result);
                    }

                    return generationResult.getOutputText();
                });
    }

    private Mono<List<String>> getTopicsFlaggedByModeration(String input, ModerationConfigurationRequest moderation) {

        return textModerationPort.moderate(input)
                .map(result -> {
                    if (moderation.isAbsolute()) {
                        if (result.isContentFlagged()) {
                            return result.getFlaggedTopics();
                        }

                        return emptyList();
                    }

                    return result.getModerationScores()
                            .entrySet()
                            .stream()
                            .filter(entry -> isTopicFlagged(entry, moderation))
                            .map(Map.Entry::getKey)
                            .toList();
                });
    }

    private boolean isTopicFlagged(Entry<String, Double> entry, ModerationConfigurationRequest moderation) {

        if (isEmpty(moderation.getThresholds())) {
            return false;
        }

        return entry.getValue() > moderation.getThresholds().get(entry.getKey());
    }
}
