package me.moirai.discordbot.core.application.usecase.world.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class DeleteWorld extends UseCase<Void> {

    private final String id;
    private final String requesterDiscordId;

    private DeleteWorld(String id, String requesterDiscordId) {

        this.id = id;
        this.requesterDiscordId = requesterDiscordId;
    }

    public static DeleteWorld build(String id, String requesterDiscordId) {

        return new DeleteWorld(id, requesterDiscordId);
    }

    public String getId() {
        return id;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }
}
