package me.moirai.discordbot.core.application.usecase.discord.userdetails;

import io.micrometer.common.util.StringUtils;
import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.request.SignupUser;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.CreateUserResult;
import me.moirai.discordbot.core.domain.userdetails.User;
import me.moirai.discordbot.core.domain.userdetails.UserDomainRepository;

@UseCaseHandler
public class SignupUserHandler extends AbstractUseCaseHandler<SignupUser, CreateUserResult> {

    private final UserDomainRepository repository;

    public SignupUserHandler(UserDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validate(SignupUser useCase) {

        if (StringUtils.isBlank(useCase.getDiscordId())) {
            throw new IllegalArgumentException("Discord ID cannot be null");
        }
    }

    @Override
    public CreateUserResult execute(SignupUser useCase) {

        User user = repository.save(User.builder()
                .discordId(useCase.getDiscordId())
                .creatorDiscordId(useCase.getDiscordId())
                .build());

        return CreateUserResult.builder()
                .id(user.getId())
                .discordId(user.getDiscordId())
                .creationDate(user.getCreationDate())
                .build();
    }
}
