package es.thalesalv.chatrpg.application.service.commands.dmassist;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.ContextDatastore;
import es.thalesalv.chatrpg.application.config.BotConfig;
import es.thalesalv.chatrpg.application.config.CommandEventData;
import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.commands.lorebook.CommandService;
import es.thalesalv.chatrpg.domain.exception.BotSlashCommandException;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Service
@RequiredArgsConstructor
public class EditDMAssistService implements CommandService {

    private final BotConfig botConfig;
    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;

    private static final String ERROR_EDITING = "Error editing message";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when editing the message. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(EditDMAssistService.class);

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for message edition");
        event.deferReply();
        botConfig.getPersonas().forEach(persona -> {
            try {
                final boolean isCurrentChannel = persona.getChannelIds().stream().anyMatch(id -> event.getChannel().getId().equals(id));
                if (isCurrentChannel) {
                    final Message message = Optional.ofNullable(event.getOption("message-id"))
                        .filter(a -> a != null)
                        .map(opt -> {
                            final String messageId = opt.getAsString();
                            try {
                                return event.getChannel().retrieveMessageById(messageId).submit()
                                        .whenComplete((msg, error) -> {
                                            if (error != null)
                                                throw new DiscordFunctionException("Failed to retrieve message for editing", error);

                                            final Modal editMessageModal = buildEditMessageModal(msg);
                                            event.replyModal(editMessageModal).queue();
                                        }).get();
                            } catch (InterruptedException | ExecutionException e) {
                                LOGGER.error(ERROR_EDITING, e);
                                throw new BotSlashCommandException(e);
                            }
                        })
                        .orElseGet(() -> {
                            final MessageChannelUnion channel = event.getChannel();
                            final SelfUser bot = event.getJDA().getSelfUser();
                            final MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
                            final Message msg = history.getRetrievedHistory().stream()
                                    .filter(a -> a.getAuthor().getId().equals(bot.getId()))
                                    .findFirst()
                                    .get();

                            final Modal editMessageModal = buildEditMessageModal(msg);
                            event.replyModal(editMessageModal).queue();
                            return msg;
                        });

                    contextDatastore.setCommandEventData(CommandEventData.builder()
                            .messageToBeEdited(message)
                            .persona(persona)
                            .build());
                }
            } catch (Exception e) {
                LOGGER.error(ERROR_EDITING, e);
                event.reply(SOMETHING_WRONG_TRY_AGAIN)
                        .setEphemeral(true).queue();
            }
        });
    }

    @Override
    public void handle(ModalInteractionEvent event) {

        LOGGER.debug("Received data of edit message modal");
        try {
            event.deferReply();
            final String messageContent = event.getValue("message-content").getAsString();
            final CommandEventData eventData = contextDatastore.getCommandEventData();
            final Message message = eventData.getMessageToBeEdited();
            moderationService.moderate(messageContent, eventData, event)
                    .subscribe(response -> message.editMessage(messageContent).submit()
                        .whenComplete((msg, error) -> {
                            if (error != null)
                                throw new DiscordFunctionException("Error in message edition modal", error);
        
                            event.reply("Message has been edited").setEphemeral(true).queue();
                        }));
        } catch (Exception e) {
            LOGGER.error(ERROR_EDITING, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true).queue();
        }
    }

    private Modal buildEditMessageModal(Message msg) {

        LOGGER.debug("Building message edition modal");
        final TextInput messageContent = TextInput
                .create("message-content", "Message content", TextInputStyle.PARAGRAPH)
                .setPlaceholder("The Forest of the Talking Trees is located in the west of the country.")
                .setValue(msg.getContentDisplay())
                .setMaxLength(2000)
                .setRequired(true)
                .build();

        return Modal.create("edit-message-dmassist-modal", "Edit message content")
                .addComponents(ActionRow.of(messageContent)).build();
    }
}
