package me.moirai.discordbot.core.domain.userdetails;

import static me.moirai.discordbot.core.domain.userdetails.Role.ADMIN;
import static me.moirai.discordbot.core.domain.userdetails.Role.PLAYER;

import java.time.OffsetDateTime;

public class UserFixture {

    public static User.Builder player() {

        return User.builder()
                .discordId("12345")
                .role(PLAYER)
                .version(1)
                .creatorDiscordId("12341234")
                .creationDate(OffsetDateTime.now())
                .lastUpdateDate(OffsetDateTime.now());
    }

    public static User.Builder admin() {

        return User.builder()
                .discordId("12345")
                .role(ADMIN)
                .version(1)
                .creatorDiscordId("12341234")
                .creationDate(OffsetDateTime.now())
                .lastUpdateDate(OffsetDateTime.now());
    }
}
