package me.moirai.discordbot.infrastructure.outbound.persistence.world;

import static me.moirai.discordbot.infrastructure.outbound.persistence.SearchPredicates.contains;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.data.domain.Sort.Direction.ASC;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldLorebookEntries;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldLorebookEntriesResult;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntry;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.mapper.WorldLorebookPersistenceMapper;

@Repository
public class WorldLorebookEntryRepositoryImpl implements WorldLorebookEntryRepository {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_ITEMS = 10;

    private static final String NAME = "name";
    private static final String DEFAULT_SORT_BY_FIELD = NAME;

    private final WorldLorebookEntryJpaRepository jpaRepository;
    private final WorldLorebookPersistenceMapper mapper;

    public WorldLorebookEntryRepositoryImpl(WorldLorebookEntryJpaRepository jpaRepository,
            WorldLorebookPersistenceMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public WorldLorebookEntry save(WorldLorebookEntry entry) {

        return jpaRepository.save(entry);
    }

    @Override
    public Optional<WorldLorebookEntry> findById(String lorebookEntryId) {

        return jpaRepository.findById(lorebookEntryId);
    }

    @Override
    public List<WorldLorebookEntry> findAllByRegex(String valueToSearch, String worldId) {

        return jpaRepository.findAllByNameRegex(valueToSearch, worldId);
    }

    @Override
    public List<WorldLorebookEntry> findAllByWorldId(String worldId) {

        return jpaRepository.findAllByWorldId(worldId);
    }

    @Override
    public void deleteById(String id) {

        jpaRepository.deleteById(id);
    }

    @Override
    public SearchWorldLorebookEntriesResult search(SearchWorldLorebookEntries request) {

        int page = extractPageNumber(request.getPage());
        int size = extractPageSize(request.getSize());
        String sortByField = extractSortByField(request.getSortingField());
        Direction direction = extractDirection(request.getDirection());

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortByField));
        Specification<WorldLorebookEntry> query = buildSearchQuery(request);
        Page<WorldLorebookEntry> pagedResult = jpaRepository.findAll(query, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    private Specification<WorldLorebookEntry> buildSearchQuery(SearchWorldLorebookEntries query) {

        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("worldId"), query.getWorldId()));

            if (StringUtils.isNotBlank(query.getName())) {
                predicates.add(contains(cb, root, NAME, query.getName()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Direction extractDirection(String direction) {
        return isBlank(direction) ? ASC : Direction.fromString(direction);
    }

    private String extractSortByField(String sortByField) {
        return isBlank(sortByField) ? DEFAULT_SORT_BY_FIELD : sortByField;
    }

    private int extractPageSize(Integer pageSize) {
        return pageSize == null ? DEFAULT_ITEMS : pageSize;
    }

    private int extractPageNumber(Integer page) {
        return page == null ? DEFAULT_PAGE : page - 1;
    }
}
