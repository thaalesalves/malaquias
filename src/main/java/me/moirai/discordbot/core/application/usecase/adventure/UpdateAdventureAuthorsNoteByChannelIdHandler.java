package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureAuthorsNoteByChannelId;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureRepository;

@UseCaseHandler
public class UpdateAdventureAuthorsNoteByChannelIdHandler
        extends AbstractUseCaseHandler<UpdateAdventureAuthorsNoteByChannelId, Void> {

    private static final String USER_NO_PERMISSION = "User does not have permission to update adventure";
    private static final String ADVENTURE_NOT_FOUND = "Adventure to be updated was not found";

    private AdventureRepository repository;

    public UpdateAdventureAuthorsNoteByChannelIdHandler(AdventureRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void execute(UpdateAdventureAuthorsNoteByChannelId useCase) {

        Adventure adventure = repository.findByDiscordChannelId(useCase.getChannelId())
                .orElseThrow(() -> new AssetNotFoundException(ADVENTURE_NOT_FOUND));

        if (!adventure.canUserWrite(useCase.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_NO_PERMISSION);
        }

        repository.updateAuthorsNoteByChannelId(useCase.getAuthorsNote(), useCase.getChannelId());
        return null;
    }
}
