package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class UpdateAdventureNudgeByChannelId extends UseCase<Void> {

    private final String nudge;
    private final String channelId;
    private final String requesterDiscordId;

    private UpdateAdventureNudgeByChannelId(Builder builder) {
        this.nudge = builder.nudge;
        this.channelId = builder.channelId;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getNudge() {
        return nudge;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String nudge;
        private String channelId;
        private String requesterDiscordId;

        public Builder nudge(String nudge) {
            this.nudge = nudge;
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

        public UpdateAdventureNudgeByChannelId build() {
            return new UpdateAdventureNudgeByChannelId(this);
        }
    }
}