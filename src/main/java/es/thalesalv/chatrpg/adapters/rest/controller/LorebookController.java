package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.application.service.LorebookService;
import es.thalesalv.chatrpg.domain.model.api.ApiErrorResponse;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lore/book")
public class LorebookController {

    private final LorebookService lorebookService;

    private static final String RETRIEVE_ALL_LOREBOOKS_REQUEST = "Received request for listing all lorebooks";
    private static final String SAVE_LOREBOOK_REQUEST = "Received request for saving lorebook -> {}";
    private static final String UPDATE_LOREBOOK_REQUEST = "Received request for updating lorebook with ID {} -> {}";
    private static final String DELETE_LOREBOOK_REQUEST = "Received request for deleting lorebook with ID {}";
    private static final String DELETE_LOREBOOK_RESPONSE = "Returning response for deleting lorebook with ID {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String ITEM_INSERTED_CANNOT_BE_NULL = "The item to be inserted cannot be null";

    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllLorebooks(@RequestHeader("requester") String requesterUserId) {

        LOGGER.info(RETRIEVE_ALL_LOREBOOKS_REQUEST);
        return Mono.just(lorebookService.retrieveAllLorebooks(requesterUserId))
                .map(this::buildResponse)
                .onErrorResume(e -> {
                    LOGGER.error("Error retrieving all lorebooks", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> saveLorebook(@RequestBody final Lorebook lorebook) {

        LOGGER.info(SAVE_LOREBOOK_REQUEST, lorebook);
        return Mono.just(lorebookService.saveLorebook(lorebook))
                .map(this::buildResponse)
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ITEM_INSERTED_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ITEM_INSERTED_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(GENERAL_ERROR_MESSAGE, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @PutMapping("{lorebook-id}")
    public Mono<ResponseEntity<ApiResponse>> updateLorebook(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "lorebook-id") final String lorebookId, @RequestBody final Lorebook lorebook) {

        LOGGER.info(UPDATE_LOREBOOK_REQUEST, lorebookId, lorebook);
        return Mono.just(lorebookService.updateLorebook(lorebookId, lorebook, requesterUserId))
                .map(this::buildResponse)
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ITEM_INSERTED_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ITEM_INSERTED_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(GENERAL_ERROR_MESSAGE, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @DeleteMapping("{lorebook-id}")
    public Mono<ResponseEntity<ApiResponse>> deleteLorebook(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "lorebook-id") final String lorebookId) {

        LOGGER.info(DELETE_LOREBOOK_REQUEST, lorebookId);
        return Mono.just(lorebookId)
                .map(id -> {
                    lorebookService.deleteLorebook(lorebookId, requesterUserId);
                    LOGGER.info(DELETE_LOREBOOK_RESPONSE, lorebookId);
                    return ResponseEntity.ok()
                            .body(ApiResponse.empty());
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ITEM_INSERTED_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ITEM_INSERTED_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(GENERAL_ERROR_MESSAGE, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    private ResponseEntity<ApiResponse> buildResponse(List<Lorebook> lorebooks) {

        LOGGER.info("Sending response for lorebooks -> {}", lorebooks);
        final ApiResponse respose = ApiResponse.builder()
                .lorebooks(lorebooks)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ResponseEntity<ApiResponse> buildResponse(Lorebook lorebook) {

        LOGGER.info("Sending response for lorebooks -> {}", lorebook);
        final ApiResponse respose = ApiResponse.builder()
                .lorebook(lorebook)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ApiResponse buildErrorResponse(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for lorebooks");
        return ApiResponse.builder()
                .error(ApiErrorResponse.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }
}
