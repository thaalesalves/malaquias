package me.moirai.discordbot.infrastructure.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.port.DiscordAuthenticationPort;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.request.GetUserDetailsByDiscordId;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.UserDetailsResult;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.DiscordUserDataResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class MoiraiUserDetailsServiceTest {

    @Mock
    private DiscordAuthenticationPort discordAuthenticationPort;

    @Mock
    private UseCaseRunner useCaseRunner;

    @InjectMocks
    private MoiraiUserDetailsService service;

    @Test
    public void authenticateUser_whenUserExists_thenReturnPrincipal() {

        // Given
        String token = "TOKEN";
        String username = "john.doe";
        String nickname = "JohnDoe";

        DiscordUserDataResponse response = DiscordUserDataResponse.builder()
                .globalNickname(nickname)
                .username(username)
                .email("email@email.com")
                .build();

        when(useCaseRunner.run(any(GetUserDetailsByDiscordId.class))).thenReturn(UserDetailsResult.builder()
                .avatarUrl("http://someurl.com/somepic.jpg")
                .discordId("12345")
                .nickname(nickname)
                .username(username)
                .joinDate(OffsetDateTime.parse("2024-12-01T14:00:00Z"))
                .build());

        when(discordAuthenticationPort.retrieveLoggedUser(anyString())).thenReturn(Mono.just(response));

        // Then
        StepVerifier.create(service.findByUsername(token))
                .assertNext(userDetails -> {
                    assertThat(userDetails).isNotNull();
                    assertThat(userDetails.getUsername()).isEqualTo(response.getUsername());
                })
                .verifyComplete();
    }
}
