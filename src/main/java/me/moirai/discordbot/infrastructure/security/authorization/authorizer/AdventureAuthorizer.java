package me.moirai.discordbot.infrastructure.security.authorization.authorizer;

import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureById;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;
import me.moirai.discordbot.infrastructure.security.authentication.MoiraiPrincipal;
import me.moirai.discordbot.infrastructure.security.authentication.SecuritySessionContext;
import me.moirai.discordbot.infrastructure.security.authorization.BaseAssetAuthorizer;

@Component
public class AdventureAuthorizer implements BaseAssetAuthorizer {

    private static final String ADMIN = "ADMIN";
    private static final String PUBLIC = "PUBLIC";

    private final UseCaseRunner useCaseRunner;

    public AdventureAuthorizer(UseCaseRunner useCaseRunner) {
        this.useCaseRunner = useCaseRunner;
    }

    @Override
    public String getAssetType() {
        return "Adventure";
    }

    @Override
    public boolean isOwner(String assetId, String userId) {

        MoiraiPrincipal principal = SecuritySessionContext.getAuthenticatedUser();
        GetAdventureById request = GetAdventureById.build(assetId, principal.getDiscordId());
        GetAdventureResult adventureDetails = useCaseRunner.run(request);

        boolean isAdmin = principal.getRole().equals(ADMIN);
        boolean isOwner = adventureDetails.getOwnerDiscordId().equals(userId);

        return isAdmin || isOwner;
    }

    @Override
    public boolean canModify(String assetId, String userId) {

        MoiraiPrincipal principal = SecuritySessionContext.getAuthenticatedUser();
        GetAdventureById request = GetAdventureById.build(assetId, principal.getDiscordId());
        GetAdventureResult adventureDetails = useCaseRunner.run(request);

        boolean isAdmin = principal.getRole().equals(ADMIN);
        boolean isOwner = adventureDetails.getOwnerDiscordId().equals(userId);
        boolean isWriter = adventureDetails.getUsersAllowedToWrite().contains(userId);

        return isAdmin || isOwner || isWriter;
    }

    @Override
    public boolean canRead(String assetId, String userId) {

        MoiraiPrincipal principal = SecuritySessionContext.getAuthenticatedUser();
        GetAdventureById request = GetAdventureById.build(assetId, principal.getDiscordId());
        GetAdventureResult adventureDetails = useCaseRunner.run(request);

        boolean isAdmin = principal.getRole().equals(ADMIN);
        boolean isOwner = adventureDetails.getOwnerDiscordId().equals(userId);
        boolean isWriter = adventureDetails.getUsersAllowedToWrite().contains(userId);
        boolean isReader = adventureDetails.getUsersAllowedToRead().contains(userId);
        boolean isPublic = adventureDetails.getVisibility().equalsIgnoreCase(PUBLIC);

        return isAdmin || isOwner || isWriter || isReader || isPublic;
    }
}
