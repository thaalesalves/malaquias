package me.moirai.discordbot.core.domain.persona;

import me.moirai.discordbot.core.application.usecase.persona.request.CreatePersona;
import reactor.core.publisher.Mono;

public interface PersonaService {

    Mono<Persona> createFrom(CreatePersona command);
}
