package me.moirai.discordbot.core.application.usecase.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchFavoriteAdventures;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;

@ExtendWith(MockitoExtension.class)
public class SearchFavoriteAdventuresHandlerTest {

    @Mock
    private AdventureQueryRepository repository;

    @InjectMocks
    private SearchFavoriteAdventuresHandler handler;

    @Test
    public void searchAdventures() {

        // Given
        SearchFavoriteAdventures query = SearchFavoriteAdventures.builder()
                .direction("ASC")
                .page(1)
                .items(2)
                .sortByField("name")
                .build();

        SearchAdventuresResult expectedResult = SearchAdventuresResult.builder()
                .page(1)
                .items(2)
                .build();

        when(repository.search(any(SearchFavoriteAdventures.class)))
                .thenReturn(expectedResult);

        // When
        SearchAdventuresResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEqualTo(expectedResult.getItems());
        assertThat(result.getPage()).isEqualTo(expectedResult.getPage());
    }
}