package me.moirai.discordbot.core.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import me.moirai.discordbot.common.dbutil.AssetBaseDataAssigner;

@MappedSuperclass
@EntityListeners(AssetBaseDataAssigner.class)
public abstract class Asset {

    @Column(name = "creator_discord_id")
    protected String creatorDiscordId;

    @Column(name = "creation_date", nullable = false)
    protected OffsetDateTime creationDate;

    @Column(name = "last_update_date", nullable = false)
    protected OffsetDateTime lastUpdateDate;

    @Version
    private int version;

    protected Asset(String creatorDiscordId,
            OffsetDateTime creationDate,
            OffsetDateTime lastUpdateDate,
            int version) {

        this.creatorDiscordId = creatorDiscordId;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.version = version;
    }

    protected Asset() {
        super();
    }

    public String getCreatorDiscordId() {
        return creatorDiscordId;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public OffsetDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public int getVersion() {
        return version;
    }

    public void setCreatorDiscordId(String creatorDiscordId) {
        this.creatorDiscordId = creatorDiscordId;
    }

    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastUpdateDate(OffsetDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
