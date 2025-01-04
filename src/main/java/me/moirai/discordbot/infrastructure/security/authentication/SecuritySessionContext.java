package me.moirai.discordbot.infrastructure.security.authentication;

public class SecuritySessionContext {

    private static final ThreadLocal<DiscordPrincipal> authenticatedUser = new ThreadLocal<>();

    public static void setCurrentUser(DiscordPrincipal principal) {
        authenticatedUser.set(principal);
    }

    public static DiscordPrincipal getCurrentUser() {
        return authenticatedUser.get();
    }

    public static void clear() {
        authenticatedUser.remove();
    }
}
