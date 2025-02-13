package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;

public final class GetAdventureByChannelId extends UseCase<GetAdventureResult> {

    private final String channelId;
    private final String requesterDiscordId;

    private GetAdventureByChannelId(String channelId, String requesterDiscordId) {
        this.channelId = channelId;
        this.requesterDiscordId = requesterDiscordId;
    }

    public static GetAdventureByChannelId build(String channelId, String requesterDiscordId) {
        return new GetAdventureByChannelId(channelId, requesterDiscordId);
    }

    public String getChannelId() {
        return channelId;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }
}
