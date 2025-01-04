package me.moirai.discordbot.core.domain;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ShareableAsset extends Asset {

    @Embedded
    private Permissions permissions;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private Visibility visibility;

    protected ShareableAsset(String creatorDiscordId, OffsetDateTime creationDate,
            OffsetDateTime lastUpdateDate, Permissions permissions, Visibility visibility, int version) {

        super(creatorDiscordId, creationDate, lastUpdateDate, version);
        this.permissions = permissions;
        this.visibility = visibility;
    }

    protected ShareableAsset() {
        super();
    }

    public boolean isPublic() {

        return this.visibility.equals(Visibility.PUBLIC);
    }

    public void makePublic() {

        this.visibility = Visibility.PUBLIC;
    }

    public void makePrivate() {

        this.visibility = Visibility.PRIVATE;
    }

    public Visibility getVisibility() {

        return visibility;
    }

    public boolean isOwner(String discordUserId) {

        return permissions.getOwnerDiscordId().equals(discordUserId);
    }

    public boolean canUserWrite(String discordUserId) {

        boolean isWriter = permissions.getUsersAllowedToWrite().contains(discordUserId);

        return isOwner(discordUserId) || isWriter;
    }

    public boolean canUserRead(String discordUserId) {

        boolean isReader = permissions.getUsersAllowedToRead().contains(discordUserId);

        return canUserWrite(discordUserId) || isReader || isPublic();
    }

    public void addWriterUser(String discordUserId) {

        Permissions newPermissions = this.permissions
                .allowUserToWrite(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = newPermissions;
    }

    public void addReaderUser(String discordUserId) {

        Permissions newPermissions = this.permissions
                .allowUserToRead(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = newPermissions;
    }

    public void removeWriterUser(String discordUserId) {

        Permissions newPermissions = this.permissions
                .disallowUserToWrite(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = newPermissions;
    }

    public void removeReaderUser(String discordUserId) {

        Permissions newPermissions = this.permissions
                .disallowUserToRead(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = newPermissions;
    }

    public List<String> getUsersAllowedToWrite() {

        return Collections.unmodifiableList(this.permissions.getUsersAllowedToWrite());
    }

    public List<String> getUsersAllowedToRead() {

        return Collections.unmodifiableList(this.permissions.getUsersAllowedToRead());
    }

    public String getOwnerDiscordId() {

        return this.permissions.getOwnerDiscordId();
    }
}
