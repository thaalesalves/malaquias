package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class UpdateAdventureRememberByChannelId extends UseCase<Void> {

    private final String remember;
    private final String channelId;
    private final String requesterDiscordId;

    private UpdateAdventureRememberByChannelId(Builder builder) {
        this.remember = builder.remember;
        this.channelId = builder.channelId;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getRemember() {
        return remember;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String remember;
        private String channelId;
        private String requesterDiscordId;

        public Builder remember(String remember) {
            this.remember = remember;
            return this;
        }

        public Builder channelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public UpdateAdventureRememberByChannelId build() {
            return new UpdateAdventureRememberByChannelId(this);
        }
    }
}