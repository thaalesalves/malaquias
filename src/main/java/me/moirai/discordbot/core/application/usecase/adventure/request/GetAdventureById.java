package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;

public final class GetAdventureById extends UseCase<GetAdventureResult> {

    private final String id;

    private GetAdventureById(String id) {
        this.id = id;
    }

    public static GetAdventureById build(String id) {

        return new GetAdventureById(id);
    }

    public String getId() {
        return id;
    }
}
