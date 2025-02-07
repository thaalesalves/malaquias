package me.moirai.discordbot.infrastructure.inbound.api.mapper;

import org.springframework.stereotype.Component;

import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.CreateUserResult;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.UserDetailsResult;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreateUserResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;

@Component
public class UserDataResponseMapper {

    public UserDataResponse toResponse(UserDetailsResult discordUser) {

        return UserDataResponse.builder()
                .discordId(discordUser.getDiscordId())
                .avatar(discordUser.getAvatarUrl())
                .nickname(discordUser.getNickname())
                .username(discordUser.getUsername())
                .joinDate(discordUser.getJoinDate())
                .build();
    }

    public CreateUserResponse toResponse(CreateUserResult result) {

        return CreateUserResponse.builder()
                .discordId(result.getDiscordId())
                .creationDate(result.getCreationDate())
                .build();
    }
}
