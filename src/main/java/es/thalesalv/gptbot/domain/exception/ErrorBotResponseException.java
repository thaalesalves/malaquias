package es.thalesalv.gptbot.domain.exception;

import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;
import lombok.Getter;

public class ErrorBotResponseException extends RuntimeException {

    @Getter
    private final GptResponse response;

    public ErrorBotResponseException(String msg, GptResponse response) {

        super(msg);
        this.response = response;
    }
}
