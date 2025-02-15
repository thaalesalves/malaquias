package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class UpdateAdventureAuthorsNoteByChannelId extends UseCase<Void> {

    private final String authorsNote;
    private final String channelId;
    private final String requesterDiscordId;

    private UpdateAdventureAuthorsNoteByChannelId(Builder builder) {
        this.authorsNote = builder.authorsNote;
        this.channelId = builder.channelId;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAuthorsNote() {
        return authorsNote;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String authorsNote;
        private String channelId;
        private String requesterDiscordId;

        public Builder authorsNote(String authorsNote) {
            this.authorsNote = authorsNote;
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

        public UpdateAdventureAuthorsNoteByChannelId build() {
            return new UpdateAdventureAuthorsNoteByChannelId(this);
        }
    }
}