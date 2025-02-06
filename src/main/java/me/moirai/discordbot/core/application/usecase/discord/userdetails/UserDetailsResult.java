package me.moirai.discordbot.core.application.usecase.discord.userdetails;

import java.time.OffsetDateTime;

public final class UserDetailsResult {

    private final String discordId;
    private final String username;
    private final String nickname;
    private final String avatarUrl;
    private final OffsetDateTime joinDate;

    private UserDetailsResult(Builder builder) {
        this.discordId = builder.discordId;
        this.username = builder.username;
        this.nickname = builder.nickname;
        this.avatarUrl = builder.avatarUrl;
        this.joinDate = builder.joinDate;
    }

    public static Builder builder() {

        return new Builder();
    }

    public String getDiscordId() {
        return discordId;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public OffsetDateTime getJoinDate() {
        return joinDate;
    }

    public static final class Builder {

        private String discordId;
        private String username;
        private String nickname;
        private String avatarUrl;
        private OffsetDateTime joinDate;

        public Builder discordId(String discordId) {
            this.discordId = discordId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder joinDate(OffsetDateTime joinDate) {
            this.joinDate = joinDate;
            return this;
        }

        public UserDetailsResult build() {
            return new UserDetailsResult(this);
        }
    }
}
