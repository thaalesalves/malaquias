package me.moirai.discordbot.infrastructure.inbound.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.common.web.SecurityContextAware;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.GetUserDetailsById;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.UserDataResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@Tag(name = "User details", description = "Endpoints for user details in MoirAI")
public class UserDetailsController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;
    private final UserDataResponseMapper responseMapper;

    public UserDetailsController(UseCaseRunner useCaseRunner,
            UserDataResponseMapper responseMapper) {

        this.useCaseRunner = useCaseRunner;
        this.responseMapper = responseMapper;
    }

    @GetMapping("/{discordUserId}")
    public Mono<UserDataResponse> getMethodName(
            @PathVariable(required = true) String discordUserId) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            GetUserDetailsById query = GetUserDetailsById.build(discordUserId);
            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }
}
