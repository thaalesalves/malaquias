package me.moirai.discordbot.core.application.usecase.world.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldResult;

public final class GetWorldById extends UseCase<GetWorldResult> {

    private final String id;
    private final String requesterDiscordId;

    private GetWorldById(String id, String requesterDiscordId) {

        this.id = id;
        this.requesterDiscordId = requesterDiscordId;
    }

    public static GetWorldById build(String id, String requesterDiscordId) {

        return new GetWorldById(id, requesterDiscordId);
    }

    public String getId() {
        return id;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }
}
