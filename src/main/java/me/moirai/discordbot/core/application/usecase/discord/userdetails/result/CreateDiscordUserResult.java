package me.moirai.discordbot.core.application.usecase.discord.userdetails.result;

public final class CreateDiscordUserResult {

    private final String id;
    private final String discordId;

    private CreateDiscordUserResult(String id, String discordId) {
        this.id = id;
        this.discordId = discordId;
    }

    public static CreateDiscordUserResult build(String id, String discordId) {

        return new CreateDiscordUserResult(id, discordId);
    }

    public String getId() {
        return id;
    }

    public String getDiscordId() {
        return discordId;
    }
}
