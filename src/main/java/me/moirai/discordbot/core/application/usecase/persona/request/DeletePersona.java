package me.moirai.discordbot.core.application.usecase.persona.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class DeletePersona extends UseCase<Void> {

    private final String id;

    private DeletePersona(String id) {

        this.id = id;
    }

    public static DeletePersona build(String id) {

        return new DeletePersona(id);
    }

    public String getId() {
        return id;
    }
}
