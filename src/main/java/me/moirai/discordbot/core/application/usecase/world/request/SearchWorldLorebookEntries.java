package me.moirai.discordbot.core.application.usecase.world.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldLorebookEntriesResult;

public final class SearchWorldLorebookEntries extends UseCase<SearchWorldLorebookEntriesResult> {

    private final String name;
    private final String worldId;
    private final Integer page;
    private final Integer size;
    private final String sortingField;
    private final String direction;
    private final String requesterDiscordId;

    private SearchWorldLorebookEntries(Builder builder) {

        this.page = builder.page;
        this.size = builder.size;
        this.sortingField = builder.sortingField;
        this.direction = builder.direction;
        this.name = builder.name;
        this.worldId = builder.worldId;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

    public String getSortingField() {
        return sortingField;
    }

    public String getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    public String getWorldId() {
        return worldId;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private Integer page;
        private Integer size;
        private String sortingField;
        private String direction;
        private String name;
        private String worldId;
        private String requesterDiscordId;

        private Builder() {
        }

        public Builder page(Integer page) {
            this.page = page;
            return this;
        }

        public Builder size(Integer size) {
            this.size = size;
            return this;
        }

        public Builder sortingField(String sortingField) {
            this.sortingField = sortingField;
            return this;
        }

        public Builder direction(String direction) {
            this.direction = direction;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder worldId(String worldId) {
            this.worldId = worldId;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public SearchWorldLorebookEntries build() {
            return new SearchWorldLorebookEntries(this);
        }
    }
}