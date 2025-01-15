package me.moirai.discordbot.core.application.usecase.discord.userdetails;

import me.moirai.discordbot.common.usecases.UseCase;

public final class CreateDiscordUser extends UseCase<CreateDiscordUserResult> {

    private final String discordId;

    private CreateDiscordUser(String discordId) {
        this.discordId = discordId;
    }

    public static CreateDiscordUser build(String discordId) {
        return new CreateDiscordUser(discordId);
    }

    public String getDiscordId() {
        return discordId;
    }
}
