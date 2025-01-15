package me.moirai.discordbot.core.domain.userdetails;

import java.time.OffsetDateTime;

public class UserFixture {

    public static User.Builder sample() {

        return User.builder()
                .creationDate(OffsetDateTime.parse("2024-12-01T14:00:00Z"))
                .discordId("12345")
                .version(1)
                .creationDate(OffsetDateTime.now())
                .lastUpdateDate(OffsetDateTime.now());
    }
}
