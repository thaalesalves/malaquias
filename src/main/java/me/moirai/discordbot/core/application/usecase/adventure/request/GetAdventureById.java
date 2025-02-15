package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;

public final class GetAdventureById extends UseCase<GetAdventureResult> {

    private final String id;
    private final String requesterDiscordId;

    private GetAdventureById(String id, String requesterDiscordId) {
        this.id = id;
        this.requesterDiscordId = requesterDiscordId;
    }

    public static GetAdventureById build(String id, String requesterDiscordId) {
        return new GetAdventureById(id, requesterDiscordId);
    }

    public String getId() {
        return id;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }
}
