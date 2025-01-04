package me.moirai.discordbot.core.domain;

import static java.util.Collections.disjoint;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Formula;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import me.moirai.discordbot.common.dbutil.StringListConverter;
import me.moirai.discordbot.common.exception.BusinessRuleViolationException;

@Embeddable
public final class Permissions {

    @Column(name = "owner_discord_id")
    private String ownerDiscordId;

    @Column(name = "discord_users_allowed_to_read")
    @Convert(converter = StringListConverter.class)
    private List<String> usersAllowedToRead;

    @Formula(value = "discord_users_allowed_to_read")
    private String usersAllowedToReadString;

    @Column(name = "discord_users_allowed_to_write")
    @Convert(converter = StringListConverter.class)
    private List<String> usersAllowedToWrite;

    @Formula(value = "discord_users_allowed_to_write")
    private String usersAllowedToWriteString;

    private Permissions(Builder builder) {

        this.ownerDiscordId = builder.ownerDiscordId;
        this.usersAllowedToRead = unmodifiableList(builder.usersAllowedToRead);
        this.usersAllowedToWrite = unmodifiableList(builder.usersAllowedToWrite);
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

    public List<String> getUsersAllowedToRead() {
        return usersAllowedToRead;
    }

    public List<String> getUsersAllowedToWrite() {
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
        List<String> usersAllowedToWrite = new ArrayList<>(this.usersAllowedToWrite);
        usersAllowedToWrite.add(userDiscordId);

        return cloneFrom(this).usersAllowedToWrite(usersAllowedToWrite).build();
    }

    public Permissions disallowUserToWrite(String userDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        List<String> usersAllowedToWrite = new ArrayList<>(this.usersAllowedToWrite);
        usersAllowedToWrite.remove(userDiscordId);

        return cloneFrom(this).usersAllowedToWrite(usersAllowedToWrite).build();
    }

    public Permissions allowUserToRead(String userDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        List<String> usersAllowedToRead = new ArrayList<>(this.usersAllowedToRead);
        usersAllowedToRead.add(userDiscordId);

        return cloneFrom(this).usersAllowedToRead(usersAllowedToRead).build();
    }

    public Permissions disallowUserToRead(String userDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        List<String> usersAllowedToRead = new ArrayList<>(this.usersAllowedToRead);
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

    public boolean areAllowedToWrite(List<String> discordUserIds) {

        boolean isOwnerFound = discordUserIds.stream().anyMatch(this::isOwner);

        return !disjoint(this.usersAllowedToWrite, discordUserIds) || isOwnerFound;
    }

    public boolean areAllowedToRead(List<String> discordUserIds) {

        return !disjoint(this.usersAllowedToRead, discordUserIds) || areAllowedToWrite(discordUserIds);
    }

    public static final class Builder {

        private String ownerDiscordId;
        private List<String> usersAllowedToRead = new ArrayList<>();
        private List<String> usersAllowedToWrite = new ArrayList<>();

        private Builder() {
        }

        public Builder ownerDiscordId(String ownerDiscordId) {

            this.ownerDiscordId = ownerDiscordId;
            return this;
        }

        public Builder usersAllowedToRead(List<String> usersAllowedToRead) {

            if (usersAllowedToRead != null) {
                this.usersAllowedToRead = usersAllowedToRead;
            }

            return this;
        }

        public Builder usersAllowedToWrite(List<String> usersAllowedToWrite) {

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
