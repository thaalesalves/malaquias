package me.moirai.discordbot.infrastructure.inbound.api.response;

import java.time.OffsetDateTime;

public class CreateUserResponse {

    private String discordId;
    private OffsetDateTime creationDate;

    public CreateUserResponse() {
    }

    private CreateUserResponse(Builder build) {

        this.discordId = build.discordId;
        this.creationDate = build.creationDate;
    }

    public static Builder builder() {

        return new Builder();
    }

    public String getDiscordId() {
        return discordId;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public static final class Builder {

        private String discordId;
        private OffsetDateTime creationDate;

        private Builder() {
        }

        public Builder discordId(String discordId) {
            this.discordId = discordId;
            return this;
        }

        public Builder creationDate(OffsetDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public CreateUserResponse build() {
            return new CreateUserResponse(this);
        }
    }
}