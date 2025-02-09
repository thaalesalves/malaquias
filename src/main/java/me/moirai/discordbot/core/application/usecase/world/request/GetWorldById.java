package me.moirai.discordbot.core.application.usecase.world.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldResult;

public final class GetWorldById extends UseCase<GetWorldResult> {

    private final String id;

    public GetWorldById(String id) {
        this.id = id;
    }

    public static GetWorldById build(String id) {

        return new GetWorldById(id);
    }

    public String getId() {
        return id;
    }
}
