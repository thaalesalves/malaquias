package me.moirai.discordbot.core.application.usecase.discord.userdetails;

public final class CreateDiscordUserResult {

    private final String id;

    private CreateDiscordUserResult(String id) {
        this.id = id;
    }

    public static CreateDiscordUserResult build(String id) {

        return new CreateDiscordUserResult(id);
    }

    public String getId() {
        return id;
    }
}
