package me.moirai.discordbot.core.application.usecase.persona.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResult;

public final class GetPersonaById extends UseCase<GetPersonaResult> {

    private final String id;

    public GetPersonaById(String id) {
        this.id = id;
    }

    public static GetPersonaById build(String id) {

        return new GetPersonaById(id);
    }

    public String getId() {
        return id;
    }
}
