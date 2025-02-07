package me.moirai.discordbot.core.application.usecase.discord.userdetails;

import io.micrometer.common.util.StringUtils;
import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.request.CreateDiscordUser;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.result.CreateDiscordUserResult;
import me.moirai.discordbot.core.domain.userdetails.User;
import me.moirai.discordbot.core.domain.userdetails.UserDomainRepository;

@UseCaseHandler
public class CreateDiscordUserHandler extends AbstractUseCaseHandler<CreateDiscordUser, CreateDiscordUserResult> {

    private final UserDomainRepository repository;

    public CreateDiscordUserHandler(UserDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validate(CreateDiscordUser useCase) {

        if (StringUtils.isBlank(useCase.getDiscordId())) {
            throw new IllegalArgumentException("Discord ID cannot be null");
        }
    }

    @Override
    public CreateDiscordUserResult execute(CreateDiscordUser useCase) {

        User discordUser = repository.save(User.builder()
                .discordId(useCase.getDiscordId())
                .build());

        return CreateDiscordUserResult.build(discordUser.getId(), discordUser.getDiscordId());
    }
}
