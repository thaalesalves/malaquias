package me.moirai.discordbot.infrastructure.outbound.persistence.notification;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import me.moirai.discordbot.core.domain.notification.Notification;
import me.moirai.discordbot.core.domain.notification.NotificationRepository;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;

    public NotificationRepositoryImpl(NotificationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Notification> findById(String id) {

        return jpaRepository.findById(id);
    }

    @Override
    public Notification save(Notification notification) {

        return jpaRepository.save(notification);
    }

    @Override
    public void deleteById(String id) {

        jpaRepository.deleteById(id);
    }

    @Override
    public List<Notification> findUnreadByUserId(String userId) {

        return jpaRepository.findUnreadByUserId(userId);
    }

    @Override
    public List<Notification> findReadByUserId(String userId) {

        return jpaRepository.findReadByUserId(userId);
    }
}
