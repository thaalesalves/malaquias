package me.moirai.discordbot.core.application.usecase.discord.userdetails;

import static io.micrometer.common.util.StringUtils.isBlank;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.request.DeleteUserByDiscordId;
import me.moirai.discordbot.core.domain.userdetails.User;
import me.moirai.discordbot.core.domain.userdetails.UserDomainRepository;

@UseCaseHandler
public class DeleteUserByDiscordIdHandler extends AbstractUseCaseHandler<DeleteUserByDiscordId, Void> {

    private static final String USER_NOT_REGISTERED_IN_MOIRAI = "The User with the requested ID is not registered in MoirAI";

    private final UserDomainRepository repository;

    public DeleteUserByDiscordIdHandler(UserDomainRepository repository) {

        this.repository = repository;
    }

    @Override
    public void validate(DeleteUserByDiscordId useCase) {

        if (isBlank(useCase.getDiscordUserId())) {
            throw new IllegalArgumentException("Discord ID cannot be null");
        }
    }

    @Override
    public Void execute(DeleteUserByDiscordId useCase) {

        User discordUser = repository.findByDiscordId(useCase.getDiscordUserId())
                .orElseThrow(() -> new AssetNotFoundException(USER_NOT_REGISTERED_IN_MOIRAI));

        repository.delete(discordUser);

        return null;
    }
}
