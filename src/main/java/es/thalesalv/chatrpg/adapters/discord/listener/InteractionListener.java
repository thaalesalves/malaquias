package es.thalesalv.chatrpg.adapters.discord.listener;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Service
@RequiredArgsConstructor
public class InteractionListener {

    private final BeanFactory beanFactory;

    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String LOREBOOK_ENTRY_COMMAND = "LorebookCommandService";
    private static final String COMMAND_SERVICE = "CommandService";

    private static final String LOREBOOK = "lb";

    private static final String COMMAND_IS_NULL = "Command is null";
    private static final String MISSING_COMMAND_ACTION = "Did not receive slash command action";
    private static final String UNKNOWN_ERROR = "Unknown exception caught while running commands";
    private static final String USER_COMMAND_NOT_FOUND = "User tried a command that does not exist";
    private static final String NON_EXISTING_COMMAND = "The command requested does not exist. Please try again.";
    private static final String NULL_POINTER_ERROR = "A null pointer exception was thrown during command execution.";
    private static final String SOMETHING_WENT_WRONG_ERROR = "Something went wrong with the command. Please try again.";

    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionListener.class);

    public void onSlashCommand(SlashCommandInteractionEvent event) {

        try {

            LOGGER.debug("Received slash command event -> {}", event);
            event.deferReply();
            final String eventName = event.getName();

            DiscordCommand command = null;
            switch (eventName) {

                case LOREBOOK: {

                    final String commandName = Optional.ofNullable(event.getOption("action"))
                            .map(OptionMapping::getAsString)
                            .orElseThrow(() -> new IllegalArgumentException(MISSING_COMMAND_ACTION));
                    command = (DiscordCommand) beanFactory.getBean(commandName + LOREBOOK_ENTRY_COMMAND);
                    break;
                }
                default: {

                    command = (DiscordCommand) beanFactory.getBean(eventName + COMMAND_SERVICE);
                    break;
                }
            }

            Optional.ofNullable(command)
                    .orElseThrow(() -> new NullPointerException(COMMAND_IS_NULL))
                    .handle(event);
        } catch (NoSuchBeanDefinitionException e) {

            LOGGER.info(USER_COMMAND_NOT_FOUND);
            event.reply(NON_EXISTING_COMMAND)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (NullPointerException e) {

            LOGGER.error(NULL_POINTER_ERROR, e);
            event.reply(SOMETHING_WENT_WRONG_ERROR)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {

            LOGGER.error(UNKNOWN_ERROR, e);
            event.reply(SOMETHING_WENT_WRONG_ERROR)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {

        try {

            LOGGER.debug("Received modal interaction event -> {}", event);
            event.deferReply();
            final String modalId = event.getModalId();
            final String commandName = modalId.split("-")[0];
            DiscordCommand command = null;
            if (modalId.contains(LOREBOOK)) {

                command = (DiscordCommand) beanFactory.getBean(commandName + LOREBOOK_ENTRY_COMMAND);
            } else {

                command = (DiscordCommand) beanFactory.getBean(commandName + COMMAND_SERVICE);
            }

            Optional.ofNullable(command)
                    .orElseThrow(() -> new NullPointerException(COMMAND_IS_NULL))
                    .handle(event);
        } catch (NoSuchBeanDefinitionException e) {

            LOGGER.info(USER_COMMAND_NOT_FOUND);
            event.reply(NON_EXISTING_COMMAND)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (NullPointerException e) {

            LOGGER.info(NULL_POINTER_ERROR, e);
            event.reply(SOMETHING_WENT_WRONG_ERROR)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {

            LOGGER.error(UNKNOWN_ERROR, e);
            event.reply(SOMETHING_WENT_WRONG_ERROR)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }
}
