package me.moirai.discordbot.core.application.usecase.world.request;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.world.result.CreateWorldResult;
import reactor.core.publisher.Mono;

public final class CreateWorld extends UseCase<Mono<CreateWorldResult>> {

    private final String name;
    private final String description;
    private final String adventureStart;
    private final String visibility;
    private final List<CreateWorldLorebookEntry> lorebookEntries;
    private final Set<String> usersAllowedToWrite;
    private final Set<String> usersAllowedToRead;
    private final String requesterDiscordId;

    private CreateWorld(Builder builder) {

        this.name = builder.name;
        this.description = builder.description;
        this.adventureStart = builder.adventureStart;
        this.visibility = builder.visibility;
        this.lorebookEntries = unmodifiableList(builder.lorebookEntries);
        this.usersAllowedToWrite = unmodifiableSet(builder.usersAllowedToWrite);
        this.usersAllowedToRead = unmodifiableSet(builder.usersAllowedToRead);
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
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

    public List<CreateWorldLorebookEntry> getLorebookEntries() {
        return lorebookEntries;
    }

    public Set<String> getUsersAllowedToWrite() {
        return usersAllowedToWrite;
    }

    public Set<String> getUsersAllowedToRead() {
        return usersAllowedToRead;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String name;
        private String description;
        private String adventureStart;
        private String visibility;
        private List<CreateWorldLorebookEntry> lorebookEntries = new ArrayList<>();
        private Set<String> usersAllowedToWrite = new HashSet<>();
        private Set<String> usersAllowedToRead = new HashSet<>();
        private String requesterDiscordId;

        private Builder() {
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

        public Builder lorebookEntries(List<CreateWorldLorebookEntry> lorebookEntries) {

            if (lorebookEntries != null) {
                this.lorebookEntries = lorebookEntries;
            }

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

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public CreateWorld build() {
            return new CreateWorld(this);
        }
    }
}