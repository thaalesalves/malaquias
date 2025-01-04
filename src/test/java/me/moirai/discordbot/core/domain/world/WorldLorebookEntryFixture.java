package me.moirai.discordbot.core.domain.world;

import java.time.OffsetDateTime;

public class WorldLorebookEntryFixture {

    public static WorldLorebookEntry.Builder sampleLorebookEntry() {

        WorldLorebookEntry.Builder builder = WorldLorebookEntry.builder();
        builder.id("857345HAA");
        builder.name("White River");
        builder.description("The White River goes through Falkreath, Whiterun and ends in Eastmarch.");
        builder.regex("[Ww]hite [Rr]iver");
        builder.creatorDiscordId("CRTID");
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());

        return builder;
    }

    public static WorldLorebookEntry.Builder samplePlayerCharacterLorebookEntry() {

        WorldLorebookEntry.Builder builder = WorldLorebookEntry.builder();
        builder.id("45534453");
        builder.name("Volin Habar");
        builder.description("Volin Habar is a warrior that fights with a sword.");
        builder.regex("[Vv]olin [Hh]abar|[Vv]oha");
        builder.playerDiscordId("2423423423423");
        builder.creatorDiscordId("CRTID");
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());

        return builder;
    }
}
