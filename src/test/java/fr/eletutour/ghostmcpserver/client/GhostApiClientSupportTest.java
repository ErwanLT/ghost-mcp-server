package fr.eletutour.ghostmcpserver.client;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GhostApiClientSupportTest {

    @Test
    void execute_ShouldMapTimeoutToReadableException() {
        assertThatThrownBy(() -> GhostApiClientSupport.execute(
                Mono.never(),
                "Ghost Content API",
                "fetch posts",
                Duration.ofMillis(10)
        ))
                .isInstanceOf(GhostApiException.class)
                .hasMessage("Ghost Content API timed out while trying to fetch posts");
    }
}
