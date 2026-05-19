package fr.eletutour.ghostmcpserver.client;

import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

final class GhostApiClientSupport {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
    private static final int MAX_RESPONSE_BODY_LENGTH = 500;

    private GhostApiClientSupport() {
    }

    static <T> T execute(Mono<T> response, String apiName, String operation) {
        return execute(response, apiName, operation, REQUEST_TIMEOUT);
    }

    static <T> T execute(Mono<T> response, String apiName, String operation, Duration timeout) {
        return response
                .timeout(timeout)
                .onErrorMap(error -> !(error instanceof GhostApiException),
                        error -> mapClientError(error, apiName, operation))
                .block();
    }

    static WebClient.ResponseSpec retrieve(WebClient.RequestHeadersSpec<?> requestSpec,
                                           String apiName,
                                           String operation) {
        return requestSpec.retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> mapHttpError(response, apiName, operation));
    }

    private static Mono<GhostApiException> mapHttpError(ClientResponse response,
                                                       String apiName,
                                                       String operation) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .map(body -> new GhostApiException(
                        httpMessage(response.statusCode(), apiName, operation),
                        apiName,
                        operation,
                        response.statusCode().value(),
                        truncate(body),
                        null
                ));
    }

    private static GhostApiException mapClientError(Throwable error, String apiName, String operation) {
        if (error instanceof TimeoutException) {
            return new GhostApiException(
                    "%s timed out while trying to %s".formatted(apiName, operation),
                    apiName,
                    operation,
                    null,
                    null,
                    error
            );
        }

        if (hasCause(error, DataBufferLimitException.class)) {
            return new GhostApiException(
                    "%s returned a response that is too large while trying to %s".formatted(apiName, operation),
                    apiName,
                    operation,
                    null,
                    null,
                    error
            );
        }

        if (error instanceof WebClientRequestException) {
            return new GhostApiException(
                    "%s could not be reached while trying to %s".formatted(apiName, operation),
                    apiName,
                    operation,
                    null,
                    null,
                    error
            );
        }

        if (error instanceof WebClientResponseException responseException) {
            return new GhostApiException(
                    httpMessage(responseException.getStatusCode(), apiName, operation),
                    apiName,
                    operation,
                    responseException.getStatusCode().value(),
                    truncate(responseException.getResponseBodyAsString()),
                    error
            );
        }

        return new GhostApiException(
                "%s failed while trying to %s".formatted(apiName, operation),
                apiName,
                operation,
                null,
                null,
                error
        );
    }

    private static String httpMessage(HttpStatusCode statusCode, String apiName, String operation) {
        if (statusCode == HttpStatus.UNAUTHORIZED || statusCode == HttpStatus.FORBIDDEN) {
            return "%s authentication failed while trying to %s. Check the configured Ghost API key."
                    .formatted(apiName, operation);
        }

        if (statusCode == HttpStatus.NOT_FOUND) {
            return "%s could not find the requested resource while trying to %s."
                    .formatted(apiName, operation);
        }

        if (statusCode == HttpStatus.TOO_MANY_REQUESTS) {
            return "%s rate limit was reached while trying to %s. Retry later."
                    .formatted(apiName, operation);
        }

        if (statusCode == HttpStatus.PAYLOAD_TOO_LARGE) {
            return "%s rejected the request payload while trying to %s."
                    .formatted(apiName, operation);
        }

        if (statusCode.is5xxServerError()) {
            return "%s returned a server error while trying to %s."
                    .formatted(apiName, operation);
        }

        return "%s returned HTTP %d while trying to %s."
                .formatted(apiName, statusCode.value(), operation);
    }

    private static String truncate(String body) {
        if (body == null || body.length() <= MAX_RESPONSE_BODY_LENGTH) {
            return body;
        }

        return body.substring(0, MAX_RESPONSE_BODY_LENGTH) + "...";
    }

    private static boolean hasCause(Throwable error, Class<? extends Throwable> expectedCause) {
        Throwable current = error;
        while (current != null) {
            if (expectedCause.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
