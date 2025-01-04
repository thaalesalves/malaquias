package me.moirai.discordbot.core.domain.adventure;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.Visibility;

public class AdventureFixture {

    private static final String OWNER_DISCORD_ID = "586678721356875";

    public static Adventure.Builder privateSingleplayerAdventure() {

        Adventure.Builder builder = Adventure.builder();
        builder.id("ADVID");
        builder.name("Name");
        builder.description("This is an RPG world");
        builder.adventureStart("As you enter the city, people around you start looking at you.");
        builder.worldId("WRLDID");
        builder.personaId("PRSNID");
        builder.discordChannelId("CHNLID");
        builder.moderation(Moderation.STRICT);
        builder.visibility(Visibility.fromString("PRIVATE"));
        builder.modelConfiguration(ModelConfigurationFixture.gpt4Mini().build());
        builder.permissions(PermissionsFixture.samplePermissions().build());
        builder.creatorDiscordId(OWNER_DISCORD_ID);
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());
        builder.discordChannelId("12345");
        builder.gameMode(GameMode.RPG);
        builder.isMultiplayer(false);
        builder.contextAttributes(ContextAttributesFixture.sample().build());
        builder.version(1);

        List<AdventureLorebookEntry> lorebook = new ArrayList<>();
        lorebook.add(AdventureLorebookEntryFixture.sampleLorebookEntry().build());
        lorebook.add(AdventureLorebookEntryFixture.samplePlayerCharacterLorebookEntry().build());
        builder.lorebook(lorebook);

        return builder;
    }

    public static Adventure.Builder privateMultiplayerAdventure() {

        Adventure.Builder builder = Adventure.builder();
        builder.id("ADVID");
        builder.name("Name");
        builder.description("This is an RPG world");
        builder.adventureStart("As you enter the city, people around you start looking at you.");
        builder.worldId("WRLDID");
        builder.personaId("PRSNID");
        builder.discordChannelId("CHNLID");
        builder.moderation(Moderation.STRICT);
        builder.visibility(Visibility.fromString("PRIVATE"));
        builder.modelConfiguration(ModelConfigurationFixture.gpt4Mini().build());
        builder.permissions(PermissionsFixture.samplePermissions().build());
        builder.creatorDiscordId(OWNER_DISCORD_ID);
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());
        builder.discordChannelId("12345");
        builder.gameMode(GameMode.RPG);
        builder.isMultiplayer(true);
        builder.contextAttributes(ContextAttributesFixture.sample().build());
        builder.version(1);

        return builder;
    }

    public static Adventure.Builder publicSingleplayerAdventure() {

        Adventure.Builder builder = Adventure.builder();
        builder.id("ADVID");
        builder.name("Name");
        builder.description("This is an RPG world");
        builder.adventureStart("As you enter the city, people around you start looking at you.");
        builder.worldId("WRLDID");
        builder.personaId("PRSNID");
        builder.discordChannelId("CHNLID");
        builder.moderation(Moderation.STRICT);
        builder.visibility(Visibility.fromString("PUBLIC"));
        builder.modelConfiguration(ModelConfigurationFixture.gpt4Mini().build());
        builder.permissions(PermissionsFixture.samplePermissions().build());
        builder.creatorDiscordId(OWNER_DISCORD_ID);
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());
        builder.discordChannelId("12345");
        builder.gameMode(GameMode.RPG);
        builder.isMultiplayer(false);
        builder.contextAttributes(ContextAttributesFixture.sample().build());
        builder.version(1);

        return builder;
    }

    public static Adventure.Builder publicMultiplayerAdventure() {

        Adventure.Builder builder = Adventure.builder();
        builder.id("ADVID");
        builder.name("Name");
        builder.description("This is an RPG world");
        builder.adventureStart("As you enter the city, people around you start looking at you.");
        builder.worldId("WRLDID");
        builder.personaId("PRSNID");
        builder.discordChannelId("CHNLID");
        builder.moderation(Moderation.STRICT);
        builder.visibility(Visibility.fromString("PUBLIC"));
        builder.modelConfiguration(ModelConfigurationFixture.gpt4Mini().build());
        builder.permissions(PermissionsFixture.samplePermissions().build());
        builder.creatorDiscordId(OWNER_DISCORD_ID);
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());
        builder.discordChannelId("12345");
        builder.gameMode(GameMode.RPG);
        builder.isMultiplayer(true);
        builder.contextAttributes(ContextAttributesFixture.sample().build());
        builder.version(1);

        return builder;
    }
}
