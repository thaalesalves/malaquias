package es.thalesalv.chatrpg.adapters.discord.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.EventDataMapper;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.application.service.usecases.BotUseCase;
import es.thalesalv.chatrpg.domain.enums.AIModel;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Service
@RequiredArgsConstructor
public class MessageListener {

    private final ChannelEntityToDTO channelEntityToDTO;
    private final ChannelRepository channelRepository;
    private final ApplicationContext applicationContext;
    private final EventDataMapper eventDataMapper;

    private static final String USE_CASE = "UseCase";
    private static final String MESSAGE_RECEIVED = "Received message by {} in {}: {}";
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    public void onMessageReceived(MessageReceivedEvent event) {

        if (!event.getAuthor().isBot()) {
            channelRepository.findByChannelId(event.getChannel().getId())
                    .map(channelEntityToDTO)
                    .ifPresent(channel -> {
                        LOGGER.debug(MESSAGE_RECEIVED, event.getAuthor(), event.getChannel().getName(), event.getMessage().getContentDisplay());
                        final Persona persona = channel.getChannelConfig().getPersona();
                        final ModelSettings modelSettings = channel.getChannelConfig().getSettings().getModelSettings();
                        final String completionType = AIModel.findByInternalName(modelSettings.getModelName()).getCompletionType();
                        final EventData eventData = eventDataMapper.translate(event, channel);
                        final CompletionService model = (CompletionService) applicationContext.getBean(completionType);
                        final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);
                        useCase.generateResponse(eventData, model);
                    });
        }
    }
}
