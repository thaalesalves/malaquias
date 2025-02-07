package me.moirai.discordbot.infrastructure.inbound.api.response;

import java.time.OffsetDateTime;

public class CreateUserResponseFixture {

    public static CreateUserResponse.Builder create() {

        return CreateUserResponse.builder()
                .discordId("12345")
                .creationDate(OffsetDateTime.now());
    }
}
