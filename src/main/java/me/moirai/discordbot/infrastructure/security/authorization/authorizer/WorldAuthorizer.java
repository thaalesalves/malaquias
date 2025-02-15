package me.moirai.discordbot.infrastructure.security.authorization.authorizer;

import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldById;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldResult;
import me.moirai.discordbot.infrastructure.security.authentication.MoiraiPrincipal;
import me.moirai.discordbot.infrastructure.security.authentication.SecuritySessionContext;
import me.moirai.discordbot.infrastructure.security.authorization.BaseAssetAuthorizer;

@Component
public class WorldAuthorizer implements BaseAssetAuthorizer {

    private static final String ADMIN = "ADMIN";
    private static final String PUBLIC = "PUBLIC";

    private final UseCaseRunner useCaseRunner;

    public WorldAuthorizer(UseCaseRunner useCaseRunner) {
        this.useCaseRunner = useCaseRunner;
    }

    @Override
    public String getAssetType() {
        return "World";
    }

    @Override
    public boolean isOwner(String assetId, String userId) {

        MoiraiPrincipal principal = SecuritySessionContext.getAuthenticatedUser();
        GetWorldById request = GetWorldById.build(assetId, principal.getDiscordId());
        GetWorldResult worldDetails = useCaseRunner.run(request);

        boolean isAdmin = principal.getRole().equals(ADMIN);
        boolean isOwner = worldDetails.getOwnerDiscordId().equals(userId);

        return isAdmin || isOwner;
    }

    @Override
    public boolean canModify(String assetId, String userId) {

        MoiraiPrincipal principal = SecuritySessionContext.getAuthenticatedUser();
        GetWorldById request = GetWorldById.build(assetId, principal.getDiscordId());
        GetWorldResult worldDetails = useCaseRunner.run(request);

        boolean isAdmin = principal.getRole().equals(ADMIN);
        boolean isOwner = worldDetails.getOwnerDiscordId().equals(userId);
        boolean isWriter = worldDetails.getUsersAllowedToWrite().contains(userId);

        return isAdmin || isOwner || isWriter;
    }

    @Override
    public boolean canRead(String assetId, String userId) {

        MoiraiPrincipal principal = SecuritySessionContext.getAuthenticatedUser();
        GetWorldById request = GetWorldById.build(assetId, principal.getDiscordId());
        GetWorldResult worldDetails = useCaseRunner.run(request);

        boolean isAdmin = principal.getRole().equals(ADMIN);
        boolean isOwner = worldDetails.getOwnerDiscordId().equals(userId);
        boolean isWriter = worldDetails.getUsersAllowedToWrite().contains(userId);
        boolean isReader = worldDetails.getUsersAllowedToRead().contains(userId);
        boolean isPublic = worldDetails.getVisibility().equalsIgnoreCase(PUBLIC);

        return isAdmin || isOwner || isWriter || isReader || isPublic;
    }
}
