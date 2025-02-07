package me.moirai.discordbot.core.application.usecase.discord.userdetails.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.UserDetailsResult;

public final class GetUserDetailsByDiscordId extends UseCase<UserDetailsResult> {

    private final String discordUserId;

    private GetUserDetailsByDiscordId(String discordUserId) {
        this.discordUserId = discordUserId;
    }

    public static GetUserDetailsByDiscordId build(String discordUserId) {
        return new GetUserDetailsByDiscordId(discordUserId);
    }

    public String getDiscordUserId() {
        return discordUserId;
    }
}