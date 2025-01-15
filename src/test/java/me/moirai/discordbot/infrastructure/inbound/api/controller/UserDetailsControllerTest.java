package me.moirai.discordbot.infrastructure.inbound.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import me.moirai.discordbot.AbstractRestWebTest;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.DiscordUserResult;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.GetUserDetailsById;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.UserDataResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponseFixture;
import me.moirai.discordbot.infrastructure.security.authentication.config.AuthenticationSecurityConfig;

@WebFluxTest(controllers = {
        UserDetailsController.class
}, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class,
        AuthenticationSecurityConfig.class
})
public class UserDetailsControllerTest extends AbstractRestWebTest {

    private static final String USER_ID_BASE_URL = "/user/%s";

    @MockBean
    private UserDataResponseMapper responseMapper;

    @Test
    public void http200WhenUserIsFound() {

        // Given
        String userId = "1234";
        UserDataResponse response = UserDataResponseFixture.create()
                .discordId(userId)
                .build();

        DiscordUserResult result = DiscordUserResult.builder()
                .avatarUrl(response.getAvatar())
                .discordId(response.getDiscordId())
                .nickname(response.getNickname())
                .username(response.getUsername())
                .joinDate(response.getJoinDate())
                .build();

        when(useCaseRunner.run(any(GetUserDetailsById.class))).thenReturn(result);
        when(responseMapper.toResponse(any(DiscordUserResult.class))).thenReturn(response);

        // Then
        webTestClient.get()
                .uri(String.format(USER_ID_BASE_URL, userId))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UserDataResponse.class)
                .value(r -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getDiscordId()).isEqualTo(r.getDiscordId());
                    assertThat(response.getNickname()).isEqualTo(r.getNickname());
                    assertThat(response.getUsername()).isEqualTo(r.getUsername());
                    assertThat(response.getAvatar()).isEqualTo(r.getAvatar());
                });

    }
}
