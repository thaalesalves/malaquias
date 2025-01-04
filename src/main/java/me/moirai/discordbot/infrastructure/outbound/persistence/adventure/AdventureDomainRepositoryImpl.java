package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@Repository
public class AdventureDomainRepositoryImpl implements AdventureDomainRepository {

    private final AdventureJpaRepository jpaRepository;
    private final FavoriteRepository favoriteRepository;

    public AdventureDomainRepositoryImpl(
            AdventureJpaRepository jpaRepository,
            FavoriteRepository favoriteRepository) {

        this.jpaRepository = jpaRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Adventure save(Adventure adventure) {

        return jpaRepository.save(adventure);
    }

    @Override
    public Optional<Adventure> findById(String id) {

        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {

        favoriteRepository.deleteAllByAssetId(id);
        jpaRepository.deleteById(id);
    }

    @Override
    public void updateRememberByChannelId(String remember, String channelId) {

        jpaRepository.updateRememberByChannelId(remember, channelId);
    }

    @Override
    public void updateAuthorsNoteByChannelId(String authorsNote, String channelId) {

        jpaRepository.updateAuthorsNoteByChannelId(authorsNote, channelId);
    }

    @Override
    public void updateNudgeByChannelId(String nudge, String channelId) {

        jpaRepository.updateNudgeByChannelId(nudge, channelId);
    }

    @Override
    public void updateBumpByChannelId(String bumpContent, int bumpFrequency, String channelId) {

        jpaRepository.updateBumpByChannelId(bumpContent, bumpFrequency, channelId);
    }
}
