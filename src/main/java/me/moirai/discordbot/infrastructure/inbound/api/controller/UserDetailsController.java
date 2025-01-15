package me.moirai.discordbot.infrastructure.inbound.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.common.web.SecurityContextAware;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.DiscordUserResult;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.GetUserDetailsById;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@Tag(name = "Users", description = "Endpoints for managing Discord Users that are registered on MoirAI")
public class UserDetailsController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;

    public UserDetailsController(UseCaseRunner useCaseRunner) {

        this.useCaseRunner = useCaseRunner;
    }

    @GetMapping("/{discordUserId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<UserDataResponse> getDiscordUserById(@PathVariable(required = true) String discordUserId) {

        return Mono.just(GetUserDetailsById.build(discordUserId))
                .map(useCaseRunner::run)
                .map(this::toResponse);
    }

    private UserDataResponse toResponse(DiscordUserResult discordUser) {

        return UserDataResponse.builder()
                .discordId(discordUser.getDiscordId())
                .avatar(discordUser.getAvatarUrl())
                .nickname(discordUser.getNickname())
                .username(discordUser.getUsername())
                .joinDate(discordUser.getJoinDate())
                .build();
    }
}
