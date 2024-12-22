package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureByChannelId;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.GoCommand;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.RetryCommand;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.StartCommand;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeInput;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeResult;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Component
public class SlashCommandListener extends ListenerAdapter {

    private static final String CONTENT = "Content";
    private static final String TOKEN_REPLY_MESSAGE = "**Characters:** %s\n**Tokens:** %s\n**Token IDs:** %s (contains %s total tokens).";
    private static final String TOO_MUCH_CONTENT_TO_TOKENIZE = "Could not tokenize content. Too much content. Please use the web UI to tokenize large text";
    private static final int DISCORD_MAX_LENGTH = 2000;

    private final UseCaseRunner useCaseRunner;
    private final DiscordListenerErrorHandler errorHandler;
    private final DiscordListenerHelper discordListenerHelper;
    private final List<String> goCommandPhrasesBeforeRunning;
    private final List<String> goCommandPhrasesAfterRunning;
    private final List<String> retryCommandPhrasesBeforeRunning;
    private final List<String> retryCommandPhrasesAfterRunning;
    private final List<String> startCommandPhrasesBeforeRunning;
    private final List<String> startCommandPhrasesAfterRunning;

    public SlashCommandListener(
            UseCaseRunner useCaseRunner,
            DiscordListenerErrorHandler errorHandler,
            DiscordListenerHelper discordListenerHelper,
            @Value("${moirai.discord.bot.commands.go.before-running}") List<String> goCommandPhrasesBeforeRunning,
            @Value("${moirai.discord.bot.commands.go.after-running}") List<String> goCommandPhrasesAfterRunning,
            @Value("${moirai.discord.bot.commands.retry.before-running}") List<String> retryCommandPhrasesBeforeRunning,
            @Value("${moirai.discord.bot.commands.retry.after-running}") List<String> retryCommandPhrasesAfterRunning,
            @Value("${moirai.discord.bot.commands.start.before-running}") List<String> startCommandPhrasesBeforeRunning,
            @Value("${moirai.discord.bot.commands.start.after-running}") List<String> startCommandPhrasesAfterRunning) {

        this.useCaseRunner = useCaseRunner;
        this.errorHandler = errorHandler;
        this.discordListenerHelper = discordListenerHelper;
        this.goCommandPhrasesBeforeRunning = goCommandPhrasesBeforeRunning;
        this.goCommandPhrasesAfterRunning = goCommandPhrasesAfterRunning;
        this.retryCommandPhrasesBeforeRunning = retryCommandPhrasesBeforeRunning;
        this.retryCommandPhrasesAfterRunning = retryCommandPhrasesAfterRunning;
        this.startCommandPhrasesBeforeRunning = startCommandPhrasesBeforeRunning;
        this.startCommandPhrasesAfterRunning = startCommandPhrasesAfterRunning;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        try {
            String command = event.getFullCommandName();
            TextChannel textChannel = event.getChannel().asTextChannel();
            Guild guild = event.getGuild();
            Member bot = guild.retrieveMember(event.getJDA().getSelfUser()).complete();
            User author = event.getMember().getUser();

            if (!author.isBot()) {
                event.getChannel().sendTyping().complete();

                switch (command) {
                    case "retry" -> {
                        InteractionHook interactionHook = discordListenerHelper.sendNotification(event,
                                getCommandPhrase(retryCommandPhrasesBeforeRunning));

                        String botUsername = bot.getUser().getName();
                        String botNickname = isNotBlank(bot.getNickname()) ? bot.getNickname()
                                : botUsername;

                        RetryCommand useCase = RetryCommand.builder()
                                .botId(bot.getId())
                                .botNickname(botNickname)
                                .botUsername(botUsername)
                                .guildId(guild.getId())
                                .channelId(textChannel.getId())
                                .build();

                        useCaseRunner.run(useCase)
                                .doOnSuccess(__ -> discordListenerHelper.updateNotification(interactionHook,
                                        getCommandPhrase(retryCommandPhrasesAfterRunning)))
                                .doOnError(error -> errorHandler.handleError(interactionHook, error))
                                .subscribe();
                    }
                    case "go" -> {
                        InteractionHook interactionHook = discordListenerHelper.sendNotification(event,
                                getCommandPhrase(goCommandPhrasesBeforeRunning));

                        String botUsername = bot.getUser().getName();
                        String botNickname = isNotBlank(bot.getNickname()) ? bot.getNickname()
                                : botUsername;

                        GoCommand useCase = GoCommand.builder()
                                .botId(bot.getId())
                                .botNickname(botNickname)
                                .botUsername(botUsername)
                                .guildId(guild.getId())
                                .channelId(textChannel.getId())
                                .build();

                        useCaseRunner.run(useCase)
                                .doOnSuccess(__ -> discordListenerHelper.updateNotification(interactionHook,
                                        getCommandPhrase(goCommandPhrasesAfterRunning)))
                                .doOnError(error -> errorHandler.handleError(interactionHook, error))
                                .subscribe();
                    }
                    case "start" -> {
                        InteractionHook interactionHook = discordListenerHelper.sendNotification(event,
                                getCommandPhrase(startCommandPhrasesBeforeRunning));

                        String botUsername = bot.getUser().getName();
                        String botNickname = isNotBlank(bot.getNickname()) ? bot.getNickname()
                                : botUsername;

                        StartCommand useCase = StartCommand.builder()
                                .botId(bot.getId())
                                .botNickname(botNickname)
                                .botUsername(botUsername)
                                .guildId(guild.getId())
                                .channelId(textChannel.getId())
                                .build();

                        useCaseRunner.run(useCase)
                                .doOnSuccess(__ -> discordListenerHelper.updateNotification(interactionHook,
                                        getCommandPhrase(startCommandPhrasesAfterRunning)))
                                .doOnError(error -> discordListenerHelper.updateNotification(interactionHook,
                                        error.getMessage()))
                                .subscribe();
                    }
                    case "say" -> {
                        TextInput content = TextInput.create("content", CONTENT, TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Message to be sent as the bot")
                                .setMinLength(1)
                                .setMaxLength(2000)
                                .build();

                        Modal modal = Modal.create("sayAsBot", "Say as bot")
                                .addComponents(ActionRow.of(content))
                                .build();

                        event.replyModal(modal).complete();
                    }
                    case "tokenize" -> {
                        InteractionHook interactionHook = discordListenerHelper.sendNotification(event,
                                "Tokenizing input...");
                        String inputToBeTokenized = event.getOption("input").getAsString();

                        TokenizeResult tokenizationResult = useCaseRunner.run(TokenizeInput.build(inputToBeTokenized))
                                .orElseThrow(() -> new IllegalStateException("Error tokenizing input"));

                        String finalResult = mapTokenizationResultToMessage(tokenizationResult);

                        if (finalResult.length() > DISCORD_MAX_LENGTH) {
                            discordListenerHelper.updateNotification(interactionHook, TOO_MUCH_CONTENT_TO_TOKENIZE);
                            return;
                        }

                        discordListenerHelper.updateNotification(interactionHook, finalResult);
                    }
                    case "remember" -> {
                        GetAdventureByChannelId request = GetAdventureByChannelId.build(textChannel.getId());
                        GetAdventureResult result = useCaseRunner.run(request);

                        TextInput rememberContent = TextInput
                                .create("rememberContent", CONTENT, TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Important piece of information the AI has to remember about")
                                .setMinLength(1)
                                .setMaxLength(50)
                                .setValue(result.getRemember())
                                .build();

                        Modal modal = Modal.create("remember", "Context modifier: remember")
                                .addComponents(ActionRow.of(rememberContent))
                                .build();

                        event.replyModal(modal).complete();
                    }
                    case "nudge" -> {
                        GetAdventureByChannelId request = GetAdventureByChannelId.build(textChannel.getId());
                        GetAdventureResult result = useCaseRunner.run(request);

                        TextInput nudgeContent = TextInput.create("nudgeContent", CONTENT, TextInputStyle.PARAGRAPH)
                                .setPlaceholder("General instructions for the AI to follow")
                                .setMinLength(1)
                                .setMaxLength(50)
                                .setValue(result.getNudge())
                                .build();

                        Modal modal = Modal.create("nudge", "Context modifier: nudge")
                                .addComponents(ActionRow.of(nudgeContent))
                                .build();

                        event.replyModal(modal).complete();
                    }
                    case "authorsnote" -> {
                        GetAdventureByChannelId request = GetAdventureByChannelId.build(textChannel.getId());
                        GetAdventureResult result = useCaseRunner.run(request);

                        TextInput authorsNoteContent = TextInput
                                .create("authorsNoteContent", CONTENT, TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Instructions from the author on how the story should be told")
                                .setMinLength(1)
                                .setMaxLength(50)
                                .setValue(result.getAuthorsNote())
                                .build();

                        Modal modal = Modal.create("authorsNote", "Context modifier: author's note")
                                .addComponents(ActionRow.of(authorsNoteContent))
                                .build();

                        event.replyModal(modal).complete();
                    }
                    case "bump" -> {
                        GetAdventureByChannelId request = GetAdventureByChannelId.build(textChannel.getId());
                        GetAdventureResult result = useCaseRunner.run(request);

                        TextInput bumpContent = TextInput.create("bumpContent", CONTENT, TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Reminders inserted between messages to keep AI's act on track")
                                .setMinLength(1)
                                .setMaxLength(50)
                                .setValue(result.getBump())
                                .build();

                        TextInput bumpFrequency = TextInput
                                .create("bumpFrequency", "Insert bump every # messages", TextInputStyle.SHORT)
                                .setPlaceholder("3")
                                .setMinLength(1)
                                .setMaxLength(2)
                                .setValue(String.valueOf(result.getBumpFrequency()))
                                .build();

                        Modal modal = Modal.create("bump", "Context modifier: bump")
                                .addComponents(ActionRow.of(bumpContent), ActionRow.of(bumpFrequency))
                                .build();

                        event.replyModal(modal).complete();
                    }
                }
            }
        } catch (Exception e) {
            handleError(event, e);
        }
    }

    private String mapTokenizationResultToMessage(TokenizeResult tokenizationResult) {

        return String.format(TOKEN_REPLY_MESSAGE, tokenizationResult.getCharacterCount(),
                tokenizationResult.getTokens(), Arrays.toString(tokenizationResult.getTokenIds()),
                tokenizationResult.getTokenCount());
    }

    private String getCommandPhrase(List<String> phraseList) {

        int randomIndex = new Random().nextInt(phraseList.size());
        return phraseList.get(randomIndex);
    }

    private void handleError(SlashCommandInteractionEvent event, Throwable error) {

        Member member = event.getMember();
        MessageChannelUnion channel = event.getChannel();
        errorHandler.handleError(member, channel, error);
    }
}
