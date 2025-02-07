package me.moirai.discordbot.core.application.usecase.discord.userdetails.result;

import java.time.OffsetDateTime;

public final class CreateUserResult {

    private final String id;
    private final String discordId;
    private final OffsetDateTime creationDate;

    private CreateUserResult(Builder build) {
        this.id = build.id;
        this.discordId = build.discordId;
        this.creationDate = build.creationDate;
    }

    public static Builder builder() {

        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getDiscordId() {
        return discordId;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public static final class Builder {

        private String id;
        private String discordId;
        private OffsetDateTime creationDate;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder discordId(String discordId) {
            this.discordId = discordId;
            return this;
        }

        public Builder creationDate(OffsetDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public CreateUserResult build() {
            return new CreateUserResult(this);
        }
    }
}
