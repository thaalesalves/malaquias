package me.moirai.discordbot.core.application.usecase.persona.result;

import static java.util.Collections.unmodifiableSet;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public final class GetPersonaResult {

    private final String id;
    private final String name;
    private final String personality;
    private final String visibility;
    private final String ownerDiscordId;
    private final Set<String> usersAllowedToWrite;
    private final Set<String> usersAllowedToRead;
    private final OffsetDateTime creationDate;
    private final OffsetDateTime lastUpdateDate;

    private GetPersonaResult(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.personality = builder.personality;
        this.visibility = builder.visibility;
        this.ownerDiscordId = builder.ownerDiscordId;
        this.usersAllowedToWrite = unmodifiableSet(builder.usersAllowedToWrite);
        this.usersAllowedToRead = unmodifiableSet(builder.usersAllowedToRead);
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

    public String getPersonality() {
        return personality;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getOwnerDiscordId() {
        return ownerDiscordId;
    }

    public Set<String> getUsersAllowedToWrite() {
        return usersAllowedToWrite;
    }

    public Set<String> getUsersAllowedToRead() {
        return usersAllowedToRead;
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
        private String personality;
        private String visibility;
        private String ownerDiscordId;
        private Set<String> usersAllowedToWrite = new HashSet<>();
        private Set<String> usersAllowedToRead = new HashSet<>();
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

        public Builder personality(String personality) {
            this.personality = personality;
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

        public Builder usersAllowedToWrite(Set<String> usersAllowedToWrite) {

            if (usersAllowedToWrite != null) {
                this.usersAllowedToWrite = usersAllowedToWrite;
            }

            return this;
        }

        public Builder usersAllowedToRead(Set<String> usersAllowedToRead) {

            if (usersAllowedToRead != null) {
                this.usersAllowedToRead = usersAllowedToRead;
            }

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

        public GetPersonaResult build() {
            return new GetPersonaResult(this);
        }
    }
}