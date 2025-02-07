package me.moirai.discordbot.core.application.usecase.discord.userdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.discord.userdetails.request.SignupUser;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.CreateUserResult;
import me.moirai.discordbot.core.domain.userdetails.User;
import me.moirai.discordbot.core.domain.userdetails.UserDomainRepository;

@ExtendWith(MockitoExtension.class)
public class SignupUserHandlerTest {

    @Mock
    private UserDomainRepository repository;

    @InjectMocks
    private SignupUserHandler handler;

    @Test
    public void createUser_whenUserIdIsNull_thenThrowException() {

        // Given
        String expectedMessage = "Discord ID cannot be null";
        String userId = null;
        SignupUser query = SignupUser.build(userId);

        // Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> handler.handle(query))
                .withMessage(expectedMessage);
    }

    @Test
    public void createUser_whenDataIsValid_thenUserIsCreated() {

        // Given
        String userId = "12345";
        SignupUser query = SignupUser.build(userId);

        User user = User.builder()
                .id("QWEQWE")
                .build();

        when(repository.save(any())).thenReturn(user);

        // When
        CreateUserResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull().isEqualTo(user.getId());
    }
}
