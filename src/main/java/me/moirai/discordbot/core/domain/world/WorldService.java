package me.moirai.discordbot.core.domain.world;

import me.moirai.discordbot.core.application.usecase.world.request.CreateWorld;
import me.moirai.discordbot.core.application.usecase.world.request.CreateWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldLorebookEntryById;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorldLorebookEntry;
import reactor.core.publisher.Mono;

public interface WorldService {

    Mono<World> createFrom(CreateWorld command);

    WorldLorebookEntry createLorebookEntry(CreateWorldLorebookEntry command);

    WorldLorebookEntry updateLorebookEntry(UpdateWorldLorebookEntry command);

    WorldLorebookEntry findLorebookEntryById(GetWorldLorebookEntryById query);

    void deleteLorebookEntry(DeleteWorldLorebookEntry command);
}
