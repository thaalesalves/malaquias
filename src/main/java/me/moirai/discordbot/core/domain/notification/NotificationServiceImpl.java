package me.moirai.discordbot.core.domain.notification;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import me.moirai.discordbot.common.annotation.DomainService;
import me.moirai.discordbot.core.application.usecase.notification.request.SendNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@DomainService
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final Sinks.Many<Notification> sink;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @Override
    public Notification sendNotification(SendNotification createNotification) {

        Notification notification = notificationRepository.save(Notification.builder()
                .isGlobal(createNotification.isGlobal())
                .isInteractable(createNotification.isInteractable())
                .message(createNotification.getMessage())
                .metadata(createNotification.getMetadata())
                .receiverDiscordId(createNotification.getReceiverDiscordId())
                .senderDiscordId(createNotification.getSenderDiscordId())
                .type(NotificationType.fromString(createNotification.getType()))
                .build());

        sink.tryEmitNext(notification);

        return notification;
    }

    @Override
    public Flux<Notification> streamNotificationsForUser(String userId) {

        return sink.asFlux()
                .filter(notification -> belongsToUser(userId, notification))
                .filter(notification -> !notification.isReadByUserId(userId));
    }

    private boolean belongsToUser(String userId, Notification notification) {

        return notification.isGlobal() || defaultIfBlank(notification.getReceiverDiscordId(), EMPTY).equals(userId);
    }
}
