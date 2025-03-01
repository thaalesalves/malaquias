package me.moirai.discordbot.core.application.usecase.adventure;

import static io.micrometer.common.util.StringUtils.isBlank;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.CreateAdventureLorebookEntry;
import me.moirai.discordbot.core.application.usecase.adventure.result.CreateAdventureLorebookEntryResult;
import me.moirai.discordbot.core.domain.adventure.AdventureService;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class CreateAdventureLorebookEntryHandler
        extends AbstractUseCaseHandler<CreateAdventureLorebookEntry, Mono<CreateAdventureLorebookEntryResult>> {

    private final AdventureService domainService;

    public CreateAdventureLorebookEntryHandler(AdventureService domainService) {
        this.domainService = domainService;
    }

    @Override
    public void validate(CreateAdventureLorebookEntry command) {

        if (isBlank(command.getAdventureId())) {

            throw new IllegalArgumentException("Adventure ID cannot be null");
        }

        if (isBlank(command.getName())) {

            throw new IllegalArgumentException("Adventure name cannot be null");
        }

        if (isBlank(command.getDescription())) {

            throw new IllegalArgumentException("Adventure description cannot be null");
        }
    }

    @Override
    public Mono<CreateAdventureLorebookEntryResult> execute(CreateAdventureLorebookEntry command) {

        return domainService.createLorebookEntry(command)
                .map(entry -> CreateAdventureLorebookEntryResult.build(entry.getId()));
    }
}
