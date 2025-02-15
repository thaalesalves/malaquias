package me.moirai.discordbot.core.domain.world;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import me.moirai.discordbot.common.annotation.NanoId;
import me.moirai.discordbot.core.domain.Asset;

@Entity(name = "WorldLorebookEntry")
@Table(name = "world_lorebook")
public class WorldLorebookEntry extends Asset {

    @Id
    @NanoId
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "regex", nullable = false)
    private String regex;

    @Column(name = "world_id", nullable = false)
    private String worldId;

    private WorldLorebookEntry(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate, builder.lastUpdateDate, builder.version);

        this.id = builder.id;
        this.name = builder.name;
        this.regex = builder.regex;
        this.description = builder.description;
        this.worldId = builder.worldId;
    }

    protected WorldLorebookEntry() {
        super();
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

    public String getRegex() {
        return regex;
    }

    public String getDescription() {
        return description;
    }

    public String getWorldId() {
        return worldId;
    }

    public void updateName(String name) {

        this.name = name;
    }

    public void updateDescription(String description) {

        this.description = description;
    }

    public void updateRegex(String regex) {

        this.regex = regex;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String regex;
        private String description;
        private String creatorDiscordId;
        private String worldId;
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;
        private int version;

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

        public Builder regex(String regex) {

            this.regex = regex;
            return this;
        }

        public Builder worldId(String worldId) {

            this.worldId = worldId;
            return this;
        }

        public Builder creatorDiscordId(String creatorDiscordId) {

            this.creatorDiscordId = creatorDiscordId;
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

        public Builder version(int version) {

            this.version = version;
            return this;
        }

        public WorldLorebookEntry build() {

            return new WorldLorebookEntry(this);
        }
    }
}
