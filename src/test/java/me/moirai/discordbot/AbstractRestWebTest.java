package me.moirai.discordbot;

import static me.moirai.discordbot.infrastructure.security.authentication.MoiraiCookie.SESSION_COOKIE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.reactive.server.WebTestClient;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.infrastructure.security.authentication.MoiraiPrincipal;
import me.moirai.discordbot.infrastructure.security.authentication.MoiraiUserDetailsService;
import net.dv8tion.jda.api.JDA;
import reactor.core.publisher.Mono;

@WebFluxTest
@ExtendWith(MockitoExtension.class)
public abstract class AbstractRestWebTest {

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

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
                .defaultCookie(SESSION_COOKIE.getName(), "COOKIE")
                .build();

        when(discordUserDetailsService.findByUsername(anyString())).thenReturn(Mono.just(userDetails));

        ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext));
        ReactiveSecurityContextHolder.withAuthentication(authentication);
    }
}
