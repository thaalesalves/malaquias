package me.moirai.discordbot.infrastructure.outbound.persistence.userdetails;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.domain.userdetails.User;
import me.moirai.discordbot.core.domain.userdetails.UserDomainRepository;
import me.moirai.discordbot.core.domain.userdetails.UserFixture;

public class UserDomainRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserDomainRepository repository;

    @Autowired
    private UserJpaRepository jpaRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void findUserByDiscordId_whenUserNotFound_thenReturnEmptyOptional() {

        // Given
        String userId = "123123";

        // When
        Optional<User> result = repository.findByDiscordId(userId);

        // Then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    public void findUserById_whenUserFound_thenReturnUser() {

        // Given
        User user = repository.save(UserFixture.player()
                .id(null)
                .build());

        // When
        Optional<User> result = repository.findByDiscordId(user.getDiscordId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getDiscordId()).isEqualTo(user.getDiscordId());
    }

    @Test
    public void createUser() {

        // Given
        User user = UserFixture.player()
                .id(null)
                .build();

        // When
        User result = repository.save(user);

        // Then
        assertThat(jpaRepository.existsById(result.getId())).isTrue();
    }

    @Test
    public void deleteUser() {

        // Given
        User user = repository.save(UserFixture.player()
                .id(null)
                .build());

        // When
        repository.delete(user);

        // Then
        assertThat(jpaRepository.existsById(user.getId())).isFalse();
    }
}
