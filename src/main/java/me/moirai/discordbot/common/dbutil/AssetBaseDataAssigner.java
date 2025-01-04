package me.moirai.discordbot.common.dbutil;

import java.time.OffsetDateTime;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import me.moirai.discordbot.core.domain.Asset;

public class AssetBaseDataAssigner {

    @PreUpdate
    @PrePersist
    public void setDate(Asset asset) {

        OffsetDateTime now = OffsetDateTime.now();
        if (asset.getCreationDate() == null) {
            asset.setCreationDate(now);
        }

        asset.setLastUpdateDate(now);
    }
}
