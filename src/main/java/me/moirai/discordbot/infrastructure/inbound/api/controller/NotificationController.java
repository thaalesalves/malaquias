package me.moirai.discordbot.infrastructure.inbound.api.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.common.web.SecurityContextAware;
import me.moirai.discordbot.core.application.usecase.notification.request.SendNotification;
import me.moirai.discordbot.core.application.usecase.notification.request.StreamNotificationsForUser;
import me.moirai.discordbot.core.application.usecase.notification.result.NotificationResult;
import me.moirai.discordbot.core.application.usecase.notification.result.SendNotificationResult;
import me.moirai.discordbot.infrastructure.inbound.api.request.SendNotificationRequest;
import me.moirai.discordbot.infrastructure.inbound.api.response.NotificationResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.SendNotificationResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/notification")
public class NotificationController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;

    public NotificationController(UseCaseRunner useCaseRunner) {
        this.useCaseRunner = useCaseRunner;
    }

    @GetMapping(value = "/{receiverDiscordId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificationResponse> subscribeToNotifications(@PathVariable String receiverDiscordId) {

        StreamNotificationsForUser query = StreamNotificationsForUser.create(receiverDiscordId);
        return useCaseRunner.run(query).map(this::toResponse);
    }

    @PostMapping
    public Mono<SendNotificationResponse> sendNotification(@RequestBody @Valid SendNotificationRequest request) {

        return mapWithAuthenticatedUser(authenticatedUser -> {
            SendNotification command = SendNotification.builder()
                    .isGlobal(request.isGlobal())
                    .isInteractable(request.isInteractable())
                    .message(request.getMessage())
                    .metadata(request.getMetadata())
                    .receiverDiscordId(request.getReceiverDiscordId())
                    .senderDiscordId(request.getSenderDiscordId())
                    .type(request.getType())
                    .build();

            SendNotificationResult result = useCaseRunner.run(command);
            return SendNotificationResponse.withIdAndCreationDateTime(result.getId(), result.getCreationDateTime());
        });
    }

    private NotificationResponse toResponse(NotificationResult result) {

        return NotificationResponse.builder()
                .isGlobal(result.isGlobal())
                .isInteractable(result.isInteractable())
                .message(result.getMessage())
                .metadata(result.getMetadata())
                .receiverDiscordId(result.getReceiverDiscordId())
                .senderDiscordId(result.getSenderDiscordId())
                .type(result.getType())
                .build();
    }
}
