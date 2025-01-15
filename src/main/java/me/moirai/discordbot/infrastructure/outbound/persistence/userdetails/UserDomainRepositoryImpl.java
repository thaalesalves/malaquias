package me.moirai.discordbot.infrastructure.outbound.persistence.userdetails;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import me.moirai.discordbot.core.domain.userdetails.User;
import me.moirai.discordbot.core.domain.userdetails.UserDomainRepository;

@Repository
public class UserDomainRepositoryImpl implements UserDomainRepository {

    private final UserJpaRepository jpaRepository;

    public UserDomainRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findByDiscordId(String discordId) {
        return jpaRepository.findByDiscordId(discordId);
    }

    @Override
    public User save(User discordUser) {
        return jpaRepository.save(discordUser);
    }

    @Override
    public void deleteByDiscordId(String discordId) {
        jpaRepository.deleteByDiscordId(discordId);
    }
}
