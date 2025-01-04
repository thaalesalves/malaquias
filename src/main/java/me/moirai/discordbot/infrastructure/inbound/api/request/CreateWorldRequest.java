package me.moirai.discordbot.infrastructure.inbound.api.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CreateWorldRequest {

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String name;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String description;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String adventureStart;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String visibility;
    private List<String> usersAllowedToWrite;
    private List<String> usersAllowedToRead;
    private List<CreateLorebookEntryRequest> lorebook;

    public CreateWorldRequest() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAdventureStart() {
        return adventureStart;
    }

    public String getVisibility() {
        return visibility;
    }

    public List<String> getUsersAllowedToWrite() {
        return usersAllowedToWrite;
    }

    public List<String> getUsersAllowedToRead() {
        return usersAllowedToRead;
    }

    public List<CreateLorebookEntryRequest> getLorebook() {
        return lorebook;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAdventureStart(String adventureStart) {
        this.adventureStart = adventureStart;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setUsersAllowedToWrite(List<String> usersAllowedToWrite) {
        this.usersAllowedToWrite = usersAllowedToWrite;
    }

    public void setUsersAllowedToRead(List<String> usersAllowedToRead) {
        this.usersAllowedToRead = usersAllowedToRead;
    }

    public void setLorebook(List<CreateLorebookEntryRequest> lorebook) {
        this.lorebook = lorebook;
    }
}
