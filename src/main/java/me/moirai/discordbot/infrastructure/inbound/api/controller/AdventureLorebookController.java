package me.moirai.discordbot.infrastructure.inbound.api.controller;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.text.CaseUtils.toCamelCase;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.common.web.SecurityContextAware;
import me.moirai.discordbot.core.application.usecase.adventure.request.CreateAdventureLorebookEntry;
import me.moirai.discordbot.core.application.usecase.adventure.request.DeleteAdventureLorebookEntry;
import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureLorebookEntryById;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventureLorebookEntries;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureLorebookEntry;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.AdventureLorebookEntryRequestMapper;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.AdventureLorebookEntryResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreateLorebookEntryRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.LorebookSearchParameters;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdateLorebookEntryRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchDirection;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchSortingField;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreateLorebookEntryResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.LorebookEntryResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.SearchLorebookEntriesResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UpdateLorebookEntryResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/adventure/{adventureId}/lorebook")
@Tag(name = "Adventure Lorebooks", description = "Endpoints for managing MoirAI Adventure Lorebooks")
public class AdventureLorebookController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;
    private final AdventureLorebookEntryRequestMapper requestMapper;
    private final AdventureLorebookEntryResponseMapper responseMapper;

    public AdventureLorebookController(UseCaseRunner useCaseRunner,
            AdventureLorebookEntryRequestMapper requestMapper,
            AdventureLorebookEntryResponseMapper responseMapper) {

        this.useCaseRunner = useCaseRunner;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("canRead(#adventureId, 'Adventure')")
    public Mono<SearchLorebookEntriesResponse> search(
            @PathVariable(required = true) String adventureId,
            LorebookSearchParameters searchParameters) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchAdventureLorebookEntries query = SearchAdventureLorebookEntries.builder()
                    .page(searchParameters.getPage())
                    .size(searchParameters.getSize())
                    .sortingField(getSortingField(searchParameters.getSortingField()))
                    .direction(getDirection(searchParameters.getDirection()))
                    .name(searchParameters.getName())
                    .requesterDiscordId(authenticatedUser.getDiscordId())
                    .adventureId(adventureId)
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @GetMapping("/{entryId}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("canRead(#adventureId, 'Adventure')")
    public Mono<LorebookEntryResponse> getLorebookEntryById(
            @PathVariable(required = true) String adventureId,
            @PathVariable(required = true) String entryId) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            GetAdventureLorebookEntryById query = GetAdventureLorebookEntryById.builder()
                    .entryId(entryId)
                    .adventureId(adventureId)
                    .requesterDiscordId(authenticatedUser.getDiscordId())
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("canModify(#adventureId, 'Adventure')")
    public Mono<CreateLorebookEntryResponse> createLorebookEntry(
            @PathVariable(required = true) String adventureId,
            @Valid @RequestBody CreateLorebookEntryRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            CreateAdventureLorebookEntry command = requestMapper.toCommand(request,
                    adventureId, authenticatedUser.getDiscordId());

            return useCaseRunner.run(command)
                    .map(responseMapper::toResponse);
        });
    }

    @PutMapping("/{entryId}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("canModify(#adventureId, 'Adventure')")
    public Mono<UpdateLorebookEntryResponse> updateLorebookEntry(
            @PathVariable(required = true) String adventureId,
            @PathVariable(required = true) String entryId,
            @Valid @RequestBody UpdateLorebookEntryRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            UpdateAdventureLorebookEntry command = requestMapper.toCommand(request, entryId,
                    adventureId, authenticatedUser.getDiscordId());

            return useCaseRunner.run(command)
                    .map(responseMapper::toResponse);
        });
    }

    @DeleteMapping("/{entryId}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("canModify(#adventureId, 'Adventure')")
    public Mono<Void> deleteLorebookEntry(
            @PathVariable(required = true) String adventureId,
            @PathVariable(required = true) String entryId) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            DeleteAdventureLorebookEntry command = requestMapper.toCommand(entryId,
                    adventureId, authenticatedUser.getDiscordId());

            useCaseRunner.run(command);

            return Mono.empty();
        });
    }

    private String getSortingField(SearchSortingField searchSortingField) {

        if (searchSortingField != null) {
            return toCamelCase(searchSortingField.name(), false, '_');
        }

        return EMPTY;
    }

    private String getDirection(SearchDirection searchDirection) {

        if (searchDirection != null) {
            return toCamelCase(searchDirection.name(), false, '_');
        }

        return EMPTY;
    }
}
