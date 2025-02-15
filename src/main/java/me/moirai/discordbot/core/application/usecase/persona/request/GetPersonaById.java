package me.moirai.discordbot.core.application.usecase.persona.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResult;

public final class GetPersonaById extends UseCase<GetPersonaResult> {

    private final String id;
    private final String requesterDiscordId;

    private GetPersonaById(String id, String requesterDiscordId) {

        this.id = id;
        this.requesterDiscordId = requesterDiscordId;
    }

    public static GetPersonaById build(String id, String requesterDiscordId) {

        return new GetPersonaById(id, requesterDiscordId);
    }

    public String getId() {
        return id;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }
}