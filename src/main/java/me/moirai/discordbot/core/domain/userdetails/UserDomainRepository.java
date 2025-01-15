package me.moirai.discordbot.core.domain.userdetails;

import java.util.Optional;

public interface UserDomainRepository {

    Optional<User> findByDiscordId(String discordId);

    User save(User discordUser);

    void deleteByDiscordId(String discordId);
}
