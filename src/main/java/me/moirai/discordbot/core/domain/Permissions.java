package me.moirai.discordbot.core.domain;

import static java.util.Collections.disjoint;
import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Formula;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import me.moirai.discordbot.common.dbutil.StringSetConverter;
import me.moirai.discordbot.common.exception.BusinessRuleViolationException;

@Embeddable
public final class Permissions {

    @Column(name = "owner_discord_id")
    private String ownerDiscordId;

    @Column(name = "discord_users_allowed_to_read")
    @Convert(converter = StringSetConverter.class)
    private Set<String> usersAllowedToRead;

    @Formula(value = "discord_users_allowed_to_read")
    private String usersAllowedToReadString;

    @Column(name = "discord_users_allowed_to_write")
    @Convert(converter = StringSetConverter.class)
    private Set<String> usersAllowedToWrite;

    @Formula(value = "discord_users_allowed_to_write")
    private String usersAllowedToWriteString;

    private Permissions(Builder builder) {

        this.ownerDiscordId = builder.ownerDiscordId;
        this.usersAllowedToRead = unmodifiableSet(builder.usersAllowedToRead);
        this.usersAllowedToWrite = unmodifiableSet(builder.usersAllowedToWrite);
    }

    protected Permissions() {
        super();
    }

    public static Builder builder() {

        return new Builder();
    }

    private Builder cloneFrom(Permissions permissions) {

        return builder().ownerDiscordId(permissions.getOwnerDiscordId())
                .usersAllowedToRead(permissions.getUsersAllowedToRead())
                .usersAllowedToWrite(permissions.getUsersAllowedToWrite());
    }

    public String getOwnerDiscordId() {
        return ownerDiscordId;
    }

    public Set<String> getUsersAllowedToRead() {
        return usersAllowedToRead;
    }

    public Set<String> getUsersAllowedToWrite() {
        return usersAllowedToWrite;
    }

    public String getUsersAllowedToReadString() {
        return usersAllowedToReadString;
    }

    public String getUsersAllowedToWriteString() {
        return usersAllowedToWriteString;
    }

    public Permissions updateOwner(String newOwnerDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        return cloneFrom(this).ownerDiscordId(newOwnerDiscordId).build();
    }

    public Permissions allowUserToWrite(String userDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        Set<String> usersAllowedToWrite = new HashSet<>(this.usersAllowedToWrite);
        usersAllowedToWrite.add(userDiscordId);

        return cloneFrom(this).usersAllowedToWrite(usersAllowedToWrite).build();
    }

    public Permissions disallowUserToWrite(String userDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        Set<String> usersAllowedToWrite = new HashSet<>(this.usersAllowedToWrite);
        usersAllowedToWrite.remove(userDiscordId);

        return cloneFrom(this).usersAllowedToWrite(usersAllowedToWrite).build();
    }

    public Permissions allowUserToRead(String userDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        Set<String> usersAllowedToRead = new HashSet<>(this.usersAllowedToRead);
        usersAllowedToRead.add(userDiscordId);

        return cloneFrom(this).usersAllowedToRead(usersAllowedToRead).build();
    }

    public Permissions disallowUserToRead(String userDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        Set<String> usersAllowedToRead = new HashSet<>(this.usersAllowedToRead);
        usersAllowedToRead.remove(userDiscordId);

        return cloneFrom(this).usersAllowedToRead(usersAllowedToRead).build();
    }

    public void validateOwnership(String currentOwnerDiscordId) {

        if (!this.ownerDiscordId.equals(currentOwnerDiscordId)) {
            throw new BusinessRuleViolationException("Operation not permitted: user does not own this asset");
        }
    }

    public boolean isOwner(String discordUserId) {

        return this.ownerDiscordId.equals(discordUserId);
    }

    public boolean isAllowedToWrite(String discordUserId) {

        return this.usersAllowedToWrite.contains(discordUserId) || isOwner(discordUserId);
    }

    public boolean isAllowedToRead(String discordUserId) {

        return this.usersAllowedToRead.contains(discordUserId) || isAllowedToWrite(discordUserId);
    }

    public boolean areAllowedToWrite(Set<String> discordUserIds) {

        boolean isOwnerFound = discordUserIds.stream().anyMatch(this::isOwner);

        return !disjoint(this.usersAllowedToWrite, discordUserIds) || isOwnerFound;
    }

    public boolean areAllowedToRead(Set<String> discordUserIds) {

        return !disjoint(this.usersAllowedToRead, discordUserIds) || areAllowedToWrite(discordUserIds);
    }

    public static final class Builder {

        private String ownerDiscordId;
        private Set<String> usersAllowedToRead = new HashSet<>();
        private Set<String> usersAllowedToWrite = new HashSet<>();

        private Builder() {
        }

        public Builder ownerDiscordId(String ownerDiscordId) {

            this.ownerDiscordId = ownerDiscordId;
            return this;
        }

        public Builder usersAllowedToRead(Set<String> usersAllowedToRead) {

            if (usersAllowedToRead != null) {
                this.usersAllowedToRead = usersAllowedToRead;
            }

            return this;
        }

        public Builder usersAllowedToWrite(Set<String> usersAllowedToWrite) {

            if (usersAllowedToWrite != null) {
                this.usersAllowedToWrite = usersAllowedToWrite;
            }

            return this;
        }

        public Permissions build() {

            return new Permissions(this);
        }
    }
}
