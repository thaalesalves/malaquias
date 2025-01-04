package me.moirai.discordbot.infrastructure.outbound.persistence.persona;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;
import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaDomainRepository;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

public class PersonaDomainRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PersonaDomainRepository repository;

    @Autowired
    private PersonaJpaRepository jpaRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void retrievePersonaById() {

        // Given
        Persona persona = jpaRepository.save(PersonaFixture.privatePersona()
                .id(null)
                .build());

        // When
        Optional<Persona> retrievedPersonaOptional = repository.findById(persona.getId());

        // Then
        assertThat(retrievedPersonaOptional).isNotNull().isNotEmpty();

        Persona retrievedPersona = retrievedPersonaOptional.get();
        assertThat(retrievedPersona.getId()).isEqualTo(persona.getId());
    }

    @Test
    public void createPersona() {

        // Given
        Persona persona = PersonaFixture.privatePersona()
                .id(null)
                .build();

        // When
        Persona createdPersona = repository.save(persona);

        // Then
        assertThat(createdPersona).isNotNull();

        assertThat(createdPersona.getCreationDate()).isNotNull();
        assertThat(createdPersona.getLastUpdateDate()).isNotNull();

        assertThat(createdPersona.getName()).isEqualTo(persona.getName());
        assertThat(createdPersona.getPersonality()).isEqualTo(persona.getPersonality());
        assertThat(createdPersona.getVisibility()).isEqualTo(persona.getVisibility());
        assertThat(createdPersona.getUsersAllowedToWrite()).hasSameElementsAs(persona.getUsersAllowedToWrite());
        assertThat(createdPersona.getUsersAllowedToRead()).hasSameElementsAs(persona.getUsersAllowedToRead());
    }

    @Test
    public void deletePersona() {

        // Given
        Persona persona = repository.save(PersonaFixture.privatePersona()
                .id(null)
                .build());

        // When
        repository.deleteById(persona.getId());

        // Then
        assertThat(jpaRepository.findById(persona.getId())).isNotNull().isEmpty();
    }

    @Test
    public void updatePersona() {

        // Given
        Persona originalPersona = repository.save(PersonaFixture.privatePersona()
                .id(null)
                .build());

        Persona worldToUbeUpdated = PersonaFixture.privatePersona()
                .id(originalPersona.getId())
                .visibility(Visibility.PUBLIC)
                .version(originalPersona.getVersion())
                .build();

        // When
        Persona updatedPersona = repository.save(worldToUbeUpdated);

        // Then
        assertThat(originalPersona.getVersion()).isZero();
        assertThat(updatedPersona.getVersion()).isOne();
    }

    @Test
    @Transactional
    public void deletePersona_whenIsFavorite_thenDeleteFavorites() {

        // Given
        String userId = "1234";
        Persona originalPersona = repository.save(PersonaFixture.privatePersona()
                .id(null)
                .build());

        FavoriteEntity favorite = favoriteRepository.save(FavoriteEntity.builder()
                .playerDiscordId(userId)
                .assetId(originalPersona.getId())
                .assetType("persona")
                .build());

        // When
        repository.deleteById(originalPersona.getId());

        // Then
        assertThat(repository.findById(originalPersona.getId())).isNotNull().isEmpty();
        assertThat(favoriteRepository.existsById(favorite.getId())).isFalse();
    }
}
