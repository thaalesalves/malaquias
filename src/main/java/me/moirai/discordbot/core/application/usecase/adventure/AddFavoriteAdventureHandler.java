package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.AddFavoriteAdventure;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@UseCaseHandler
public class AddFavoriteAdventureHandler extends AbstractUseCaseHandler<AddFavoriteAdventure, Void> {

    private static final String USER_NO_PERMISSION = "User does not have permission to view this adventure";
    private static final String ADVENTURE_NOT_BE_FOUND = "The adventure to be favorited could not be found";

    private static final String ASSET_TYPE = "adventure";

    private final AdventureRepository adventureQueryRepository;
    private final FavoriteRepository favoriteRepository;

    public AddFavoriteAdventureHandler(AdventureRepository adventureQueryRepository,
            FavoriteRepository favoriteRepository) {
        this.adventureQueryRepository = adventureQueryRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Void execute(AddFavoriteAdventure command) {

        Adventure adventure = adventureQueryRepository.findById(command.getAssetId())
                .orElseThrow(() -> new AssetNotFoundException(ADVENTURE_NOT_BE_FOUND));

        if (!adventure.canUserRead(command.getPlayerDiscordId())) {
            throw new AssetAccessDeniedException(USER_NO_PERMISSION);
        }

        favoriteRepository.save(FavoriteEntity.builder()
                .assetType(ASSET_TYPE)
                .assetId(command.getAssetId())
                .playerDiscordId(command.getPlayerDiscordId())
                .build());

        return null;
    }
}
