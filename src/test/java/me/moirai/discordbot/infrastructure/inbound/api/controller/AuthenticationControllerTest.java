package me.moirai.discordbot.infrastructure.inbound.api.controller;

import static me.moirai.discordbot.infrastructure.security.authentication.MoiraiCookie.SESSION_COOKIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import me.moirai.discordbot.AbstractRestWebTest;
import me.moirai.discordbot.core.application.port.DiscordAuthenticationPort;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.request.GetUserDetailsByDiscordId;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.AuthenticateUserResult;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.UserDetailsResult;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.UserDataResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponseFixture;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordTokenRevocationRequest;
import me.moirai.discordbot.infrastructure.security.authentication.config.AuthenticationSecurityConfig;
import reactor.core.publisher.Mono;

@WebFluxTest(properties = {
        "moirai.discord.oauth.client-id=clientId",
        "moirai.discord.oauth.client-secret=clientSecret",
        "moirai.discord.oauth.redirect-url=redirectUrl"
}, controllers = {
        AuthenticationController.class
}, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class,
        AuthenticationSecurityConfig.class
})
public class AuthenticationControllerTest extends AbstractRestWebTest {

    @MockBean
    protected DiscordAuthenticationPort discordAuthenticationPort;

    @MockBean
    private UserDataResponseMapper responseMapper;

    @Test
    public void exchangeCodeForToken() {

        // Given
        String code = "CODE";
        AuthenticateUserResult expectedResponse = AuthenticateUserResult.builder()
                .accessToken("TOKEN")
                .expiresIn(4324324L)
                .refreshToken("RFRSHTK")
                .scope("SCOPE")
                .build();

        when(useCaseRunner.run(any())).thenReturn(Mono.just(expectedResponse));

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/auth/code")
                        .queryParam("code", code)
                        .build())
                .exchange()
                .expectCookie().valueEquals(SESSION_COOKIE.getName(), "TOKEN");
    }

    @Test
    public void noTokenWhenExchangeCodeIsNull() {

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/auth/code")
                        .build())
                .exchange()
                .expectCookie().doesNotExist(SESSION_COOKIE.getName());
    }

    @Test
    public void logout() {

        // Given
        Duration expiredCookie = Duration.ofSeconds(0);
        when(discordAuthenticationPort.logout(any(DiscordTokenRevocationRequest.class)))
                .thenReturn(Mono.empty());

        // Then
        webTestClient.get()
                .uri("/auth/logout")
                .exchange()
                .expectCookie().maxAge(SESSION_COOKIE.getName(), expiredCookie);
    }

    @Test
    public void getAuthenticatedUser() {

        // Given
        UserDataResponse result = UserDataResponseFixture.create().build();

        when(useCaseRunner.run(any(GetUserDetailsByDiscordId.class)))
                .thenReturn(mock(UserDetailsResult.class));

        when(responseMapper.toResponse(any(UserDetailsResult.class))).thenReturn(result);

        // Then
        webTestClient.get()
                .uri("/auth/user")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UserDataResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getDiscordId()).isEqualTo(result.getDiscordId());
                    assertThat(response.getNickname()).isEqualTo(result.getNickname());
                    assertThat(response.getUsername()).isEqualTo(result.getUsername());
                    assertThat(response.getAvatar()).isEqualTo(result.getAvatar());
                });

    }
}
