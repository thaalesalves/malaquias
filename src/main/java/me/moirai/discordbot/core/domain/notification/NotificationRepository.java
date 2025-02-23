package me.moirai.discordbot.core.domain.notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Optional<Notification> findById(String id);

    Notification save(Notification notification);

    void deleteById(String id);

    List<Notification> findUnreadByUserId(String userId);

    List<Notification> findReadByUserId(String userId);
}
