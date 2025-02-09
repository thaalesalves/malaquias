package me.moirai.discordbot.core.application.usecase.world;

import static org.apache.commons.lang3.StringUtils.isBlank;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorld;
import me.moirai.discordbot.core.domain.world.WorldDomainRepository;

@UseCaseHandler
public class DeleteWorldHandler extends AbstractUseCaseHandler<DeleteWorld, Void> {

    private static final String WORLD_TO_BE_VIEWED_WAS_NOT_FOUND = "World to be viewed was not found";
    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "World ID cannot be null or empty";

    private final WorldDomainRepository repository;

    public DeleteWorldHandler(WorldDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validate(DeleteWorld command) {

        if (isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeleteWorld command) {

        repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException(WORLD_TO_BE_VIEWED_WAS_NOT_FOUND));

        repository.deleteById(command.getId());

        return null;
    }
}
