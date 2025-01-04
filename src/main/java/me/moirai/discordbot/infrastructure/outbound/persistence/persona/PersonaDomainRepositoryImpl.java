package me.moirai.discordbot.infrastructure.outbound.persistence.persona;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaDomainRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@Repository
public class PersonaDomainRepositoryImpl implements PersonaDomainRepository {

    private final PersonaJpaRepository jpaRepository;
    private final FavoriteRepository favoriteRepository;

    public PersonaDomainRepositoryImpl(PersonaJpaRepository jpaRepository,
            FavoriteRepository favoriteRepository) {

        this.jpaRepository = jpaRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Persona save(Persona persona) {

        return jpaRepository.save(persona);
    }

    @Override
    public Optional<Persona> findById(String id) {

        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {

        favoriteRepository.deleteAllByAssetId(id);

        jpaRepository.deleteById(id);
    }
}
