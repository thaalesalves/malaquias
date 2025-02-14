package me.moirai.discordbot.core.application.usecase.world;

import static org.apache.commons.lang3.StringUtils.isBlank;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldById;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldResult;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldRepository;

@UseCaseHandler
public class GetWorldByIdHandler extends AbstractUseCaseHandler<GetWorldById, GetWorldResult> {

    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "World ID cannot be null or empty";
    private static final String WORLD_NOT_FOUND = "World to be deleted was not found";

    private final WorldRepository repository;

    public GetWorldByIdHandler(WorldRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validate(GetWorldById request) {

        if (isBlank(request.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public GetWorldResult execute(GetWorldById query) {

        World world = repository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException(WORLD_NOT_FOUND));

        return mapResult(world);
    }

    private GetWorldResult mapResult(World world) {

        return GetWorldResult.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().name())
                .ownerDiscordId(world.getOwnerDiscordId())
                .creationDate(world.getCreationDate())
                .lastUpdateDate(world.getLastUpdateDate())
                .build();
    }
}
