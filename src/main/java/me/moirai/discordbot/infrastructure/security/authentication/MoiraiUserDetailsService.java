package me.moirai.discordbot.infrastructure.security.authentication;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.exception.AuthenticationFailedException;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.port.DiscordAuthenticationPort;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.GetUserDetailsById;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.UserDetailsResult;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.DiscordUserDataResponse;
import reactor.core.publisher.Mono;

@Service
public class MoiraiUserDetailsService implements ReactiveUserDetailsService {

    private static final String BEARER = "Bearer ";

    private final DiscordAuthenticationPort discordAuthenticationPort;
    private final UseCaseRunner useCaseRunner;

    public MoiraiUserDetailsService(
            DiscordAuthenticationPort discordAuthenticationPort,
            UseCaseRunner useCaseRunner) {

        this.discordAuthenticationPort = discordAuthenticationPort;
        this.useCaseRunner = useCaseRunner;
    }

    @Override
    public Mono<UserDetails> findByUsername(String token) {

        return discordAuthenticationPort.retrieveLoggedUser(token)
                .map(userDetails -> getUserDetails(userDetails, token));
    }

    private MoiraiPrincipal getUserDetails(DiscordUserDataResponse userDetails, String token) {

        try {
            GetUserDetailsById query = GetUserDetailsById.build(userDetails.getId());
            UserDetailsResult discordUserResult = useCaseRunner.run(query);

            return MoiraiPrincipal.builder()
                    .id(discordUserResult.getDiscordId())
                    .username(discordUserResult.getUsername())
                    .email(userDetails.getEmail())
                    .authorizationToken(token.replace(BEARER, EMPTY))
                    .build();
        } catch (AssetNotFoundException e) {
            throw new AuthenticationFailedException("Invalid user requested authentication", e);
        }
    }
}
