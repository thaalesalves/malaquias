package me.moirai.discordbot.core.application.usecase.world;

import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldLorebookEntryById;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldLorebookEntryResult;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntry;
import me.moirai.discordbot.core.domain.world.WorldService;

@UseCaseHandler
public class GetWorldLorebookEntryByIdHandler extends AbstractUseCaseHandler<GetWorldLorebookEntryById, GetWorldLorebookEntryResult> {

    private final WorldService domainService;

    public GetWorldLorebookEntryByIdHandler(WorldService domainService) {
        this.domainService = domainService;
    }

    @Override
    public void validate(GetWorldLorebookEntryById query) {

        if (StringUtils.isBlank(query.getEntryId())) {
            throw new IllegalArgumentException("Lorebook entry ID cannot be null");
        }

        if (StringUtils.isBlank(query.getWorldId())) {
            throw new IllegalArgumentException("World ID cannot be null");
        }
    }

    @Override
    public GetWorldLorebookEntryResult execute(GetWorldLorebookEntryById query) {

        WorldLorebookEntry entry = domainService.findLorebookEntryById(query);

        return mapResult(entry);
    }

    private GetWorldLorebookEntryResult mapResult(WorldLorebookEntry entry) {

        return GetWorldLorebookEntryResult.builder()
                .id(entry.getId())
                .name(entry.getName())
                .regex(entry.getRegex())
                .description(entry.getDescription())
                .creationDate(entry.getCreationDate())
                .lastUpdateDate(entry.getLastUpdateDate())
                .build();
    }
}
