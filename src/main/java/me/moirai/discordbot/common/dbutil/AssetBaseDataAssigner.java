package me.moirai.discordbot.common.dbutil;

import java.time.OffsetDateTime;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import me.moirai.discordbot.core.domain.Asset;
import me.moirai.discordbot.infrastructure.security.authentication.DiscordPrincipal;
import me.moirai.discordbot.infrastructure.security.authentication.SecuritySessionContext;

public class AssetBaseDataAssigner {

    @PreUpdate
    @PrePersist
    public void setBaseData(Asset asset) {

        DiscordPrincipal authenticatedUser = SecuritySessionContext.getCurrentUser();
        if (asset.getCreatorDiscordId() == null) {
            asset.setCreatorDiscordId(authenticatedUser.getId());
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (asset.getCreationDate() == null) {
            asset.setCreationDate(now);
        }

        asset.setLastUpdateDate(now);
    }
}
