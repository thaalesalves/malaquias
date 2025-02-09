package me.moirai.discordbot.core.application.usecase.discord.userdetails;

import static io.micrometer.common.util.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.exception.DiscordApiException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.DiscordUserDetailsPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.request.GetUserDetailsByDiscordId;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.UserDetailsResult;
import me.moirai.discordbot.core.domain.userdetails.User;
import me.moirai.discordbot.core.domain.userdetails.UserDomainRepository;

@UseCaseHandler
public class GetUserDetailsByDiscordIdHandler extends AbstractUseCaseHandler<GetUserDetailsByDiscordId, UserDetailsResult> {

    private static final String USER_NOT_REGISTERED_IN_MOIRAI = "The User with the requested ID is not registered in MoirAI";
    private static final String DISCORD_USER_DOES_NOT_EXIST = "The Discord User with the requested ID does not exist";

    private final UserDomainRepository repository;
    private final DiscordUserDetailsPort discordUserDetailsPort;

    public GetUserDetailsByDiscordIdHandler(
            UserDomainRepository repository,
            DiscordUserDetailsPort discordUserDetailsPort) {

        this.repository = repository;
        this.discordUserDetailsPort = discordUserDetailsPort;
    }

    @Override
    public void validate(GetUserDetailsByDiscordId useCase) {

        if (isBlank(useCase.getDiscordUserId())) {
            throw new IllegalArgumentException("Discord ID cannot be null");
        }
    }

    @Override
    public UserDetailsResult execute(GetUserDetailsByDiscordId useCase) {

        DiscordUserDetails discordDetails = discordUserDetailsPort.getUserById(useCase.getDiscordUserId())
                .orElseThrow(() -> new DiscordApiException(NOT_FOUND, DISCORD_USER_DOES_NOT_EXIST));

        User moiraiUser = repository.findByDiscordId(useCase.getDiscordUserId())
                .orElseThrow(() -> new AssetNotFoundException(USER_NOT_REGISTERED_IN_MOIRAI));

        String nickname = isBlank(discordDetails.getNickname()) ? discordDetails.getUsername()
                : discordDetails.getNickname();

        return UserDetailsResult.builder()
                .discordId(moiraiUser.getDiscordId())
                .nickname(nickname)
                .username(discordDetails.getUsername())
                .avatarUrl(discordDetails.getAvatarUrl())
                .role(moiraiUser.getRole().name())
                .joinDate(moiraiUser.getCreationDate())
                .build();
    }
}
