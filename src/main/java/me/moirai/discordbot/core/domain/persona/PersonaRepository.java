package me.moirai.discordbot.core.domain.persona;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonas;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;

public interface PersonaRepository {

    Persona save(Persona persona);

    void deleteById(String id);

    Optional<Persona> findById(String id);

    SearchPersonasResult search(SearchPersonas request);

    boolean existsById(String id);
}