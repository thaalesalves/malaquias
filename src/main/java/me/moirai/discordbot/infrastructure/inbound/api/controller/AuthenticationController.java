package me.moirai.discordbot.infrastructure.inbound.api.controller;

import static me.moirai.discordbot.infrastructure.security.authentication.MoiraiCookie.EXPIRY_COOKIE;
import static me.moirai.discordbot.infrastructure.security.authentication.MoiraiCookie.REFRESH_COOKIE;
import static me.moirai.discordbot.infrastructure.security.authentication.MoiraiCookie.SESSION_COOKIE;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import io.swagger.v3.oas.annotations.Hidden;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.common.web.SecurityContextAware;
import me.moirai.discordbot.core.application.port.DiscordAuthenticationPort;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.request.AuthenticateUser;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.request.GetUserDetailsByDiscordId;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.AuthenticateUserResult;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.UserDataResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordTokenRevocationRequest;
import me.moirai.discordbot.infrastructure.security.authentication.MoiraiCookie;
import reactor.core.publisher.Mono;

@Hidden
@RestController
@RequestMapping("/auth")
public class AuthenticationController extends SecurityContextAware {

    private static final String NONE = "None";
    private static final String ROOT = "/";
    private static final String TOKEN_TYPE_HINT = "access_token";
    private static final int EXPIRE_IMMEDIATELY = 0;
    private static final boolean SECURE = true;
    private static final HttpStatusCode REDIRECT = HttpStatusCode.valueOf(302);

    private final String clientId;
    private final String clientSecret;
    private final String successPath;
    private final String failPath;
    private final String logoutPath;
    private final DiscordAuthenticationPort discordAuthenticationPort;
    private final UseCaseRunner useCaseRunner;
    private final UserDataResponseMapper responseMapper;

    public AuthenticationController(
            @Value("${moirai.discord.oauth.client-id}") String clientId,
            @Value("${moirai.discord.oauth.client-secret}") String clientSecret,
            @Value("${moirai.discord.oauth.redirect-url}") String redirectUrl,
            @Value("${moirai.security.redirect-path.success}") String successPath,
            @Value("${moirai.security.redirect-path.fail}") String failPath,
            @Value("${moirai.security.redirect-path.logout}") String logoutPath,
            DiscordAuthenticationPort discordAuthenticationPort,
            UseCaseRunner useCaseRunner,
            UserDataResponseMapper responseMapper) {

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.successPath = successPath;
        this.logoutPath = logoutPath;
        this.failPath = failPath;
        this.discordAuthenticationPort = discordAuthenticationPort;
        this.useCaseRunner = useCaseRunner;
        this.responseMapper = responseMapper;
    }

    @GetMapping("/code")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<ServerHttpResponse> codeExchange(
            @RequestParam(required = false) String code, ServerWebExchange exchange) {

        if (isBlank(code)) {
            exchange.getResponse().setStatusCode(REDIRECT);
            exchange.getResponse().getHeaders().setLocation(URI.create(failPath));

            return Mono.just(exchange.getResponse());
        }

        AuthenticateUser command = AuthenticateUser.build(code);
        return useCaseRunner.run(command)
                .map(authResponse -> handleSessionAuthentication(exchange, authResponse));
    }

    @GetMapping("/logout")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<ServerHttpResponse> logout(ServerWebExchange exchange, Authentication authentication) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {
            DiscordTokenRevocationRequest request = DiscordTokenRevocationRequest.builder()
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .token(authenticatedUser.getAuthorizationToken())
                    .tokenTypeHint(TOKEN_TYPE_HINT)
                    .build();

            return discordAuthenticationPort.logout(request)
                    .thenReturn(handleSessionTermination(exchange));
        });
    }

    @GetMapping("/user")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<UserDataResponse> getAuthenticatedUserDetails() {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            GetUserDetailsByDiscordId query = GetUserDetailsByDiscordId.build(authenticatedUser.getDiscordId());
            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    private ServerHttpResponse handleSessionAuthentication(
            ServerWebExchange exchange, AuthenticateUserResult authResult) {

        ResponseCookie sessionCookie = createCookie(SESSION_COOKIE, authResult.getAccessToken());
        ResponseCookie refreshCookie = createCookie(REFRESH_COOKIE, authResult.getRefreshToken());
        ResponseCookie expiryCookie = createCookie(EXPIRY_COOKIE, String.valueOf(authResult.getExpiresIn()));

        exchange.getResponse().setStatusCode(REDIRECT);
        exchange.getResponse().getHeaders().setLocation(URI.create(successPath));
        exchange.getResponse().addCookie(sessionCookie);
        exchange.getResponse().addCookie(refreshCookie);
        exchange.getResponse().addCookie(expiryCookie);

        return exchange.getResponse();
    }

    private ServerHttpResponse handleSessionTermination(ServerWebExchange exchange) {

        ResponseCookie sessionCookie = expireCookie(SESSION_COOKIE);
        ResponseCookie refreshCookie = expireCookie(REFRESH_COOKIE);
        ResponseCookie expiryCookie = expireCookie(EXPIRY_COOKIE);

        exchange.getResponse().setStatusCode(REDIRECT);
        exchange.getResponse().getHeaders().setLocation(URI.create(logoutPath));
        exchange.getResponse().addCookie(sessionCookie);
        exchange.getResponse().addCookie(refreshCookie);
        exchange.getResponse().addCookie(expiryCookie);

        return exchange.getResponse();
    }

    private ResponseCookie createCookie(MoiraiCookie cookie, String cookieValue) {

        return ResponseCookie.from(cookie.getName(), cookieValue)
                .httpOnly(cookie.isHttpOnly())
                .path(ROOT)
                .sameSite(NONE)
                .secure(SECURE)
                .build();
    }

    private ResponseCookie expireCookie(MoiraiCookie cookie) {

        return ResponseCookie.from(cookie.getName(), null)
                .httpOnly(cookie.isHttpOnly())
                .path(ROOT)
                .sameSite(NONE)
                .secure(SECURE)
                .maxAge(EXPIRE_IMMEDIATELY)
                .build();
    }
}
