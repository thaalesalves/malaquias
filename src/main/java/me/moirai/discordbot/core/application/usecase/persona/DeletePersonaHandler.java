package me.moirai.discordbot.core.application.usecase.persona;

import static org.apache.commons.lang3.StringUtils.isBlank;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.persona.request.DeletePersona;
import me.moirai.discordbot.core.domain.persona.PersonaRepository;

@UseCaseHandler
public class DeletePersonaHandler extends AbstractUseCaseHandler<DeletePersona, Void> {

    private static final String PERSONA_NOT_FOUND = "Persona was not found";
    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Persona ID cannot be null or empty";

    private final PersonaRepository repository;

    public DeletePersonaHandler(PersonaRepository repository) {

        this.repository = repository;
    }

    @Override
    public void validate(DeletePersona request) {

        if (isBlank(request.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeletePersona request) {

        repository.findById(request.getId())
                .orElseThrow(() -> new AssetNotFoundException(PERSONA_NOT_FOUND));

        repository.deleteById(request.getId());

        return null;
    }
}
