package es.thalesalv.chatrpg.common.web;

import java.util.function.Function;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import es.thalesalv.chatrpg.infrastructure.security.authentication.DiscordPrincipal;
import reactor.core.publisher.Mono;

public abstract class SecurityContextAware {

    protected <T> Mono<T> mapWithAuthenticatedUser(Function<DiscordPrincipal, T> function) {

        return mapWithAuthenticatedPrincipal(authentication -> (DiscordPrincipal) authentication.getPrincipal())
                .map(function);
    }

    protected <T> Mono<T> flatMapWithAuthenticatedUser(Function<? super DiscordPrincipal, ? extends Mono<? extends T>> function) {

        return mapWithAuthenticatedPrincipal(authentication -> (DiscordPrincipal) authentication.getPrincipal())
                .flatMap(function);
    }

    protected <T> Mono<T> mapWithAuthenticatedPrincipal(Function<Authentication, T> function) {

        return mapWithSecurityContext(SecurityContext::getAuthentication)
                .map(function);
    }

    protected <T> Mono<T> mapWithSecurityContext(Function<SecurityContext, T> function) {

        return ReactiveSecurityContextHolder.getContext().map(function);
    }
}
