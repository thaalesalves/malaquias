package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;

@Component
public class ModelSettingsEntityToDTO implements Function<ModelSettingsEntity, ModelSettings> {

    @Override
    public ModelSettings apply(ModelSettingsEntity t) {

        return ModelSettings.builder()
                .id(t.getId())
                .owner(t.getOwner())
                .logitBias(t.getLogitBias())
                .maxTokens(t.getMaxTokens())
                .modelName(t.getModelName())
                .temperature(t.getTemperature())
                .stopSequence(t.getStopSequence())
                .presencePenalty(t.getPresencePenalty())
                .frequencyPenalty(t.getFrequencyPenalty())
                .chatHistoryMemory(t.getChatHistoryMemory())
                .build();
    }
}
