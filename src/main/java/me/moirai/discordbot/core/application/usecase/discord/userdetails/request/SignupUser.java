package me.moirai.discordbot.core.application.usecase.discord.userdetails.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.CreateUserResult;

public final class SignupUser extends UseCase<CreateUserResult> {

    private final String discordId;

    private SignupUser(String discordId) {
        this.discordId = discordId;
    }

    public static SignupUser build(String discordId) {
        return new SignupUser(discordId);
    }

    public String getDiscordId() {
        return discordId;
    }
}
