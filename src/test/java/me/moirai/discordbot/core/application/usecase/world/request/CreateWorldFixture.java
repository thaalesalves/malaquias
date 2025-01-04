package me.moirai.discordbot.core.application.usecase.world.request;

import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;

public class CreateWorldFixture {

    public static CreateWorld.Builder createPrivateWorld() {

        World world = WorldFixture.privateWorld().build();
        return CreateWorld.builder()
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .requesterDiscordId(world.getOwnerDiscordId())
                .usersAllowedToWrite(world.getUsersAllowedToWrite())
                .usersAllowedToRead(world.getUsersAllowedToRead());
    }
}
