package es.thalesalv.gptbot.service;

import es.thalesalv.gptbot.model.ChannelSettings;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GptService {

    private final OpenAIApiService openAiService;

    private static final String MODEL_ADA = "text-ada-001";
    private static final String MODEL_DAVINCI = "text-davinci-003";
    private static final Logger LOGGER = LoggerFactory.getLogger(GptService.class);

    public Mono<String> callModel(String prompt, String model) {

        return openAiService.callGptApi(prompt, model)
                .map(response -> {
                    try {
                        LOGGER.debug("Bot response -> {}", response);
                        return response.getChoices().get(0).getText().trim();
                    } catch (Exception e) {
                        LOGGER.error("Error processing JSON.", e);
                        throw new RuntimeException(e);
                    }
                });
    }

    public Mono<String> callDaVinci(String prompt) {

        return callModel(prompt, MODEL_DAVINCI);
    }

    public Mono<String> callAda(String prompt) {

        return callModel(prompt, MODEL_ADA);
    }
}
