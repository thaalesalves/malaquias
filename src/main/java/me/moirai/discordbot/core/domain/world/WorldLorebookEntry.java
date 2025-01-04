package me.moirai.discordbot.core.domain.world;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(name = "player_discord_id")
    private String playerDiscordId;

    @Column(name = "is_player_character", nullable = false)
    private boolean isPlayerCharacter;

    @ManyToOne
    @JoinColumn(name = "world_id", nullable = false)
    private World world;

    private WorldLorebookEntry(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate, builder.lastUpdateDate, builder.version);

        this.id = builder.id;
        this.name = builder.name;
        this.regex = builder.regex;
        this.description = builder.description;
        this.playerDiscordId = builder.playerDiscordId;
        this.isPlayerCharacter = builder.isPlayerCharacter;
        this.world = builder.world;
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

    public String getPlayerDiscordId() {
        return playerDiscordId;
    }

    public boolean isPlayerCharacter() {
        return isPlayerCharacter;
    }

    public World getWorld() {
        return world;
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

    public void assignPlayer(String playerDiscordId) {

        this.isPlayerCharacter = true;
        this.playerDiscordId = playerDiscordId;
    }

    public void unassignPlayer() {

        this.isPlayerCharacter = false;
        this.playerDiscordId = null;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String regex;
        private String description;
        private String playerDiscordId;
        private String creatorDiscordId;
        private boolean isPlayerCharacter;
        private World world;
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

        public Builder playerDiscordId(String playerDiscordId) {

            this.playerDiscordId = playerDiscordId;
            return this;
        }

        public Builder isPlayerCharacter(boolean isPlayerCharacter) {

            this.isPlayerCharacter = isPlayerCharacter;
            return this;
        }

        public Builder world(World world) {

            this.world = world;
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
