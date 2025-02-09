package me.moirai.discordbot.core.application.usecase.world.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class DeleteWorld extends UseCase<Void> {

    private final String id;

    public DeleteWorld(String id) {
        this.id = id;
    }

    public static DeleteWorld build(String id) {

        return new DeleteWorld(id);
    }

    public String getId() {
        return id;
    }
}
