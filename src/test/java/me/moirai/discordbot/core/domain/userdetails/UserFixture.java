package me.moirai.discordbot.core.domain.userdetails;

import java.time.OffsetDateTime;

public class UserFixture {

    public static User.Builder sample() {

        return User.builder()
                .discordId("12345")
                .version(1)
                .creatorDiscordId("12341234")
                .creationDate(OffsetDateTime.now())
                .lastUpdateDate(OffsetDateTime.now());
    }
}
