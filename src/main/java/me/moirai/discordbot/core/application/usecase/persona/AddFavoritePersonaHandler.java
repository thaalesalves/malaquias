package me.moirai.discordbot.core.application.usecase.persona;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.persona.request.AddFavoritePersona;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@UseCaseHandler
public class AddFavoritePersonaHandler extends AbstractUseCaseHandler<AddFavoritePersona, Void> {

    private static final String ASSET_TYPE = "persona";
    private static final String USER_NO_PERMISSION_IN_PERSONA = "User does not have permission to view the persona to be linked to this adventure";

    private final PersonaRepository personaRepository;
    private final FavoriteRepository favoriteRepository;

    public AddFavoritePersonaHandler(
            PersonaRepository personaRepository,
            FavoriteRepository favoriteRepository) {

        this.personaRepository = personaRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Void execute(AddFavoritePersona command) {

        Persona persona = personaRepository.findById(command.getAssetId())
                .orElseThrow(() -> new AssetNotFoundException("The persona to be favorited could not be found"));

        if (!persona.canUserRead(command.getPlayerDiscordId())) {
            throw new AssetAccessDeniedException(USER_NO_PERMISSION_IN_PERSONA);
        }

        favoriteRepository.save(FavoriteEntity.builder()
                .assetType(ASSET_TYPE)
                .assetId(command.getAssetId())
                .playerDiscordId(command.getPlayerDiscordId())
                .build());

        return null;
    }
}
