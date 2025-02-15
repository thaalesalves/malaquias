package me.moirai.discordbot.infrastructure.security.authentication.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.LogoutSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;

import me.moirai.discordbot.infrastructure.security.authentication.filter.AuthenticationFilter;

@Configuration
@EnableWebFluxSecurity
public class AuthenticationSecurityConfig {

    private final String[] ignoredPaths;
    private final AuthenticationFilter authenticationFilter;

    public AuthenticationSecurityConfig(
            @Value("${moirai.security.ignored-paths}") String[] ignoredPaths,
            AuthenticationFilter authenticationFilter) {

        this.ignoredPaths = ignoredPaths;
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    SecurityWebFilterChain configure(ServerHttpSecurity http) {

        HttpStatusServerEntryPoint unauthorizedEntryPoint = new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED);
        return http.httpBasic(HttpBasicSpec::disable)
                .formLogin(FormLoginSpec::disable)
                .logout(LogoutSpec::disable)
                .addFilterBefore(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges.pathMatchers(ignoredPaths).permitAll())
                .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
                .oauth2Login(withDefaults())
                .csrf(CsrfSpec::disable)
                .cors(CorsSpec::disable)
                .exceptionHandling(handler -> handler.authenticationEntryPoint(unauthorizedEntryPoint))
                .build();
    }
}
