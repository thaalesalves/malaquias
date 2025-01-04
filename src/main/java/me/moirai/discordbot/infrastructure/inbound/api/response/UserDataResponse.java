package me.moirai.discordbot.infrastructure.inbound.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDataResponse {

    private String id;
    private String username;
    private String globalNickname;
    private String avatar;
    private String email;

    public UserDataResponse() {
    }

    private UserDataResponse(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.globalNickname = builder.globalNickname;
        this.avatar = builder.avatar;
        this.email = builder.email;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getGlobalNickname() {
        return globalNickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public static final class Builder {
        private String id;
        private String username;
        private String globalNickname;
        private String avatar;
        private String email;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder globalNickname(String globalNickname) {
            this.globalNickname = globalNickname;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public UserDataResponse build() {
            return new UserDataResponse(this);
        }
    }
}