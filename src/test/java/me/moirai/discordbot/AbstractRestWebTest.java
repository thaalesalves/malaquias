package me.moirai.discordbot;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.reactive.server.WebTestClient;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.infrastructure.security.authentication.MoiraiPrincipal;
import me.moirai.discordbot.infrastructure.security.authentication.MoiraiUserDetailsService;
import me.moirai.discordbot.infrastructure.security.authentication.MoiraiCookie;
import net.dv8tion.jda.api.JDA;
import reactor.core.publisher.Mono;

@WebFluxTest
@ExtendWith(MockitoExtension.class)
public abstract class AbstractRestWebTest {

    @MockBean
    private JDA jda;

    @MockBean
    protected UseCaseRunner useCaseRunner;

    @MockBean
    protected ServerHttpSecurity serverHttpSecurity;

    @MockBean
    protected MoiraiUserDetailsService discordUserDetailsService;

    @Autowired
    protected WebTestClient webTestClient;

    @BeforeEach
    public void before() {

        UserDetails userDetails = MoiraiPrincipal.builder()
                .id("USRID")
                .email("user@email.com")
                .username("username")
                .build();

        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofMillis(3000000))
                .defaultCookie(MoiraiCookie.SESSION_COOKIE.getName(), "COOKIE")
                .build();

        when(discordUserDetailsService.findByUsername(anyString())).thenReturn(Mono.just(userDetails));
    }
}
