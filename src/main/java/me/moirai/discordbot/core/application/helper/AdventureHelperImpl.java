package me.moirai.discordbot.core.application.helper;

import me.moirai.discordbot.common.annotation.Helper;
import me.moirai.discordbot.core.domain.adventure.AdventureRepository;

@Helper
public class AdventureHelperImpl implements AdventureHelper {

    private final AdventureRepository adventureQueryRepository;

    public AdventureHelperImpl(AdventureRepository adventureQueryRepository) {
        this.adventureQueryRepository = adventureQueryRepository;
    }

    @Override
    public String getGameModeByDiscordChannelId(String channelId) {

        return adventureQueryRepository.getGameModeByDiscordChannelId(channelId);
    }
}
