package me.moirai.discordbot.core.application.usecase.discord.userdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import me.moirai.discordbot.AbstractDiscordTest;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.exception.DiscordApiException;
import me.moirai.discordbot.core.application.port.DiscordUserDetailsPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetailsFixture;
import me.moirai.discordbot.core.domain.userdetails.User;
import me.moirai.discordbot.core.domain.userdetails.UserDomainRepository;
import me.moirai.discordbot.core.domain.userdetails.UserFixture;

public class GetUserDetailsByIdHandlerTest extends AbstractDiscordTest {

    @Mock
    private UserDomainRepository repository;

    @Mock
    private DiscordUserDetailsPort discordUserDetailsPort;

    @InjectMocks
    private GetUserDetailsByIdHandler handler;

    @Test
    public void retrieveUser_whenUserIsFound_thenReturnUserData() {

        // Given
        GetUserDetailsById query = GetUserDetailsById.build("1234");
        DiscordUserDetails userDetails = DiscordUserDetailsFixture.create()
                .id(query.getDiscordUserId())
                .build();

        User user = UserFixture.sample()
                .discordId(query.getDiscordUserId())
                .build();

        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.of(userDetails));
        when(repository.findByDiscordId(anyString())).thenReturn(Optional.of(user));

        // When
        UserDetailsResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDiscordId()).isEqualTo(query.getDiscordUserId());
        assertThat(result.getNickname()).isEqualTo("natalis");
        assertThat(result.getUsername()).isEqualTo("john.natalis");
    }

    @Test
    public void retrieveUser_whenUserIsFound_andNicknameIsNull_thenReturnUserDataWithUsernameAsNickname() {

        // Given
        GetUserDetailsById query = GetUserDetailsById.build("1234");
        DiscordUserDetails userDetails = DiscordUserDetailsFixture.create()
                .id(query.getDiscordUserId())
                .nickname(null)
                .build();

        User user = UserFixture.sample()
                .discordId(query.getDiscordUserId())
                .build();

        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.of(userDetails));
        when(repository.findByDiscordId(anyString())).thenReturn(Optional.of(user));

        // When
        UserDetailsResult result = handler.execute(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDiscordId()).isEqualTo(query.getDiscordUserId());
        assertThat(result.getNickname()).isEqualTo("john.natalis");
        assertThat(result.getUsername()).isEqualTo("john.natalis");
    }

    @Test
    public void retrieveUser_whenUserNotExistsInDiscord_thenThrowException() {

        // Given
        String expectedMessage = "The Discord User with the requested ID does not exist";
        GetUserDetailsById query = GetUserDetailsById.build("1234");

        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThatExceptionOfType(DiscordApiException.class)
                .isThrownBy(() -> handler.execute(query))
                .withMessage(expectedMessage);
    }

    @Test
    public void retrieveUser_whenUserNotRegistered_thenThrowException() {

        // Given
        String expectedMessage = "The User with the requested ID is not registered in MoirAI";
        GetUserDetailsById query = GetUserDetailsById.build("1234");

        DiscordUserDetails userDetails = DiscordUserDetailsFixture.create()
                .id(query.getDiscordUserId())
                .build();

        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.of(userDetails));
        when(repository.findByDiscordId(anyString())).thenReturn(Optional.empty());

        // Then
        assertThatExceptionOfType(AssetNotFoundException.class)
                .isThrownBy(() -> handler.execute(query))
                .withMessage(expectedMessage);
    }

    @Test
    public void retrieveUser_whenUserIdIsNull_thenThrowException() {

        // Given
        String expectedMessage = "Discord ID cannot be null";
        String userId = null;
        GetUserDetailsById query = GetUserDetailsById.build(userId);

        // Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> handler.handle(query))
                .withMessage(expectedMessage);
    }
}
