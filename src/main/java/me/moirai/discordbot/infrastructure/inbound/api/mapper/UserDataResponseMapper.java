package me.moirai.discordbot.infrastructure.inbound.api.mapper;

import org.springframework.stereotype.Component;

import me.moirai.discordbot.core.application.usecase.discord.userdetails.DiscordUserResult;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;

@Component
public class UserDataResponseMapper {

    public UserDataResponse toResponse(DiscordUserResult discordUser) {

        return UserDataResponse.builder()
                .discordId(discordUser.getDiscordId())
                .avatar(discordUser.getAvatarUrl())
                .nickname(discordUser.getNickname())
                .username(discordUser.getUsername())
                .joinDate(discordUser.getJoinDate())
                .build();
    }
}
