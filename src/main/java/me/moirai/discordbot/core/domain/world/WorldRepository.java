package me.moirai.discordbot.core.domain.world;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.world.request.SearchWorlds;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;

public interface WorldRepository {

    World save(World world);

    Optional<World> findById(String id);

    void deleteById(String id);

    SearchWorldsResult search(SearchWorlds request);
}
