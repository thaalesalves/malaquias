package me.moirai.discordbot.core.application.usecase.discord.userdetails;

import me.moirai.discordbot.common.usecases.UseCase;

public final class GetUserDetailsById extends UseCase<DiscordUserResult> {

    private final String discordUserId;

    private GetUserDetailsById(String discordUserId) {
        this.discordUserId = discordUserId;
    }

    public static GetUserDetailsById build(String discordUserId) {
        return new GetUserDetailsById(discordUserId);
    }

    public String getDiscordUserId() {
        return discordUserId;
    }
}