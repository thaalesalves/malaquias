package me.moirai.discordbot.core.application.usecase.world.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.world.result.CreateWorldLorebookEntryResult;

public final class CreateWorldLorebookEntry extends UseCase<CreateWorldLorebookEntryResult> {

    private final String worldId;
    private final String name;
    private final String regex;
    private final String description;
    private final String requesterDiscordId;

    private CreateWorldLorebookEntry(Builder builder) {

        this.worldId = builder.worldId;
        this.name = builder.name;
        this.regex = builder.regex;
        this.description = builder.description;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getWorldId() {
        return worldId;
    }

    public String getName() {
        return name;
    }

    public String getRegex() {
        return regex;
    }

    public String getDescription() {
        return description;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String worldId;
        private String name;
        private String regex;
        private String description;
        private String requesterDiscordId;

        private Builder() {
        }

        public Builder worldId(String worldId) {
            this.worldId = worldId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder regex(String regex) {
            this.regex = regex;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public CreateWorldLorebookEntry build() {
            return new CreateWorldLorebookEntry(this);
        }
    }
}