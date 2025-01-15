package me.moirai.discordbot.core.domain.userdetails;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import me.moirai.discordbot.common.annotation.NanoId;
import me.moirai.discordbot.core.domain.Asset;

@Entity(name = "DiscordUser")
@Table(name = "discord_user")
public class User extends Asset {

    @Id
    @NanoId
    private String id;

    @Column(name = "discord_id", nullable = false)
    private String discordId;

    public User(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate, builder.lastUpdateDate, builder.version);

        this.id = builder.id;
        this.discordId = builder.discordId;
    }

    protected User() {
        super();
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

    public static final class Builder {

        private String id;
        private String discordId;
        private String creatorDiscordId;
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;
        private int version;

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

        public User build() {
            return new User(this);
        }
    }
}
