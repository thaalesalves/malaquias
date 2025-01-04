package me.moirai.discordbot.infrastructure.inbound.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAdventureResponse {

    private String id;

    public CreateAdventureResponse() {
    }

    private CreateAdventureResponse(String id) {
        this.id = id;
    }

    public static CreateAdventureResponse build(String id) {

        return new CreateAdventureResponse(id);
    }

    public String getId() {
        return id;
    }
}
