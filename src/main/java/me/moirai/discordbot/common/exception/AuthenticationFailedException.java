package me.moirai.discordbot.common.exception;

public class AuthenticationFailedException extends RuntimeException {

    private final String responseMessage;

    public AuthenticationFailedException(String responseMessage, String msg) {
        super(msg + ": " + responseMessage);

        this.responseMessage = responseMessage;
    }

    public AuthenticationFailedException(String responseMessage) {
        super(responseMessage);

        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
