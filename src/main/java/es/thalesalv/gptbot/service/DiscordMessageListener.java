package es.thalesalv.gptbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.thalesalv.gptbot.data.ContextDatastore;
import es.thalesalv.gptbot.model.BotSettings;
import es.thalesalv.gptbot.model.ChannelSettings;
import es.thalesalv.gptbot.usecases.ReplyQuoteUseCase;
import es.thalesalv.gptbot.usecases.TextGenerationUseCase;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscordMessageListener extends ListenerAdapter {

    @Value("#{'${config.discord.bot-channel-id}'.split(',')}")
    private List<String> botChannelId;

    @Value("${config.discord.bot-instructions}")
    private String botInstructions;

    @Value("classpath:bot-settings.json")
    private Resource botSettingsFile;

    private final ObjectMapper objectMapper;
    private final GptService gptService;
    private final ReplyQuoteUseCase replyQuoteUseCase;
    private final TextGenerationUseCase textGenerationUseCase;
    private final ContextDatastore contextDatastore;

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordMessageListener.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        try {
            final var bot = event.getJDA().getSelfUser();
            final var message = event.getMessage();
            final var channel = event.getChannel();
            final var author = event.getAuthor();

            final var botSettings = objectMapper.readValue(botSettingsFile.getContentAsString(StandardCharsets.UTF_8), BotSettings.class);
            final var isAuthorBot = author.isBot();

            final var rpgChannel = botSettings.getChannelSettings().get("rpg");
            final var chatChannel = botSettings.getChannelSettings().get("chat");

            if (!isAuthorBot) {

                LOGGER.info("{} said in {}: {}", event.getAuthor().getName(), channel.getName(), message.getContentDisplay());
                final var messages = new ArrayList<String>();
                final var replyMessage = message.getReferencedMessage();

                if (rpgChannel.getChannelIds().stream().anyMatch(id -> channel.getId().equals(id))) {
                    contextDatastore.setCurrentChannel(rpgChannel);
                    textGenerationUseCase.generateResponse(messages, channel);
                    if (replyMessage != null) {
                        replyQuoteUseCase.generateResponse(messages, author, message, replyMessage);
                    }
                } else if (chatChannel.getChannelIds().stream().anyMatch(id -> channel.getId().equals(id))) {
                    contextDatastore.setCurrentChannel(chatChannel);
                    if (replyMessage != null) {
                        replyQuoteUseCase.generateResponse(messages, author, message, replyMessage);
                    } else {
                        textGenerationUseCase.generateResponse(messages, channel);
                    }
                }

                if (contextDatastore.getCurrentChannel() != null) {
                    gptService.callDaVinci(chatifyMessages(bot, messages))
                        .filter(r -> !r.isBlank())
                        .map(response -> {
                            event.getChannel().sendMessage(response).queue();
                            return response;
                        }).subscribe();
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error parsing file or format -> {}", e);
            throw new RuntimeException(e);
        } finally {
            contextDatastore.cleanCurrentChannel();
        }
    }

    private String chatifyMessages(User bot, List<String> messages) {

        messages.add(0, contextDatastore.getCurrentChannel().getChannelInstructions()
                .replace("<BOT.NAME>", bot.getAsTag())
                .replace("<BOT.NICK>", bot.getName())
                .replace("<personality.species>", contextDatastore.getCurrentChannel().getPersonality().getSpecies())
                .replace("<personality.behavior>", contextDatastore.getCurrentChannel().getPersonality().getBehavior()));


        return new StringBuilder()
                .append(messages.stream().collect(Collectors.joining("\n")))
                .append(StringUtils.LF)
                .append(bot.getAsTag() + " said: ")
                .toString().trim();
    }
}
