package me.moirai.discordbot.core.application.usecase.adventure;

import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.DeleteAdventure;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;

@UseCaseHandler
public class DeleteAdventureHandler extends AbstractUseCaseHandler<DeleteAdventure, Void> {

    private static final String ADVENTURE_NOT_FOUND = "Adventure to be deleted was not found";
    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Adventure ID cannot be null or empty";

    private final AdventureDomainRepository repository;

    public DeleteAdventureHandler(AdventureDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validate(DeleteAdventure command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeleteAdventure command) {

        repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException(ADVENTURE_NOT_FOUND));

        repository.deleteById(command.getId());

        return null;
    }
}
