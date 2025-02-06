package me.moirai.discordbot.infrastructure.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import reactor.util.context.Context;

public final class SecuritySessionContext {

    private static final ThreadLocal<MoiraiPrincipal> authenticatedUser = new ThreadLocal<>();

    public static void setCurrentUser(MoiraiPrincipal principal) {
        authenticatedUser.set(principal);
    }

    public static MoiraiPrincipal getCurrentUser() {
        return authenticatedUser.get();
    }

    public static void clear() {
        authenticatedUser.remove();
    }

    public static Context createContext(UsernamePasswordAuthenticationToken authentication) {

        setCurrentUser((MoiraiPrincipal) authentication.getPrincipal());
        return ReactiveSecurityContextHolder.withAuthentication(authentication);
    }
}
