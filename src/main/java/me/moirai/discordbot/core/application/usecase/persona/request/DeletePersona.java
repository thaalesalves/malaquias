package me.moirai.discordbot.core.application.usecase.persona.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class DeletePersona extends UseCase<Void> {

    private final String id;
    private final String requesterDiscordId;

    private DeletePersona(String id, String requesterDiscordId) {

        this.id = id;
        this.requesterDiscordId = requesterDiscordId;
    }

    public static DeletePersona build(String id, String requesterDiscordId) {

        return new DeletePersona(id, requesterDiscordId);
    }

    public String getId() {
        return id;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }
}
