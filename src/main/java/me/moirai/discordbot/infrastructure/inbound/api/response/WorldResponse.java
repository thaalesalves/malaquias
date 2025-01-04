package me.moirai.discordbot.infrastructure.inbound.api.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorldResponse {

    private String id;
    private String name;
    private String description;
    private String adventureStart;
    private String visibility;
    private String ownerDiscordId;
    private List<String> usersAllowedToRead;
    private List<String> usersAllowedToWrite;
    private OffsetDateTime creationDate;
    private OffsetDateTime lastUpdateDate;

    public WorldResponse() {
    }

    public WorldResponse(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.adventureStart = builder.adventureStart;
        this.visibility = builder.visibility;
        this.ownerDiscordId = builder.ownerDiscordId;
        this.usersAllowedToRead = builder.usersAllowedToRead;
        this.usersAllowedToWrite = builder.usersAllowedToWrite;
        this.creationDate = builder.creationDate;
        this.lastUpdateDate = builder.lastUpdateDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAdventureStart() {
        return adventureStart;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getOwnerDiscordId() {
        return ownerDiscordId;
    }

    public List<String> getUsersAllowedToRead() {
        return usersAllowedToRead;
    }

    public List<String> getUsersAllowedToWrite() {
        return usersAllowedToWrite;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public OffsetDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public static final class Builder {
        private String id;
        private String name;
        private String description;
        private String adventureStart;
        private String visibility;
        private String ownerDiscordId;
        private List<String> usersAllowedToRead;
        private List<String> usersAllowedToWrite;
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder adventureStart(String adventureStart) {
            this.adventureStart = adventureStart;
            return this;
        }

        public Builder visibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder ownerDiscordId(String ownerDiscordId) {
            this.ownerDiscordId = ownerDiscordId;
            return this;
        }

        public Builder usersAllowedToRead(List<String> usersAllowedToRead) {
            this.usersAllowedToRead = usersAllowedToRead;
            return this;
        }

        public Builder usersAllowedToWrite(List<String> usersAllowedToWrite) {
            this.usersAllowedToWrite = usersAllowedToWrite;
            return this;
        }

        public Builder creationDate(OffsetDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder lastUpdateDate(OffsetDateTime lastUpdateDate) {
            this.lastUpdateDate = lastUpdateDate;
            return this;
        }

        public WorldResponse build() {
            return new WorldResponse(this);
        }
    }
}