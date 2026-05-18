package fr.eletutour.ghostmcpserver.configuration;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GhostPropertiesTest {

    @Test
    void hasAdminKey_ShouldReturnTrue_WhenValidKeyProvided() {
        GhostProperties properties = new GhostProperties("http://ghost.io", "id:secret", "content", "logs");
        assertThat(properties.hasAdminKey()).isTrue();
    }

    @Test
    void hasAdminKey_ShouldReturnFalse_WhenDefaultPlaceholder() {
        GhostProperties properties = new GhostProperties("http://ghost.io", "${GHOST_ADMIN_API_KEY}", "content", "logs");
        assertThat(properties.hasAdminKey()).isFalse();
    }

    @Test
    void hasAdminKey_ShouldReturnFalse_WhenNullOrBlank() {
        assertThat(new GhostProperties("url", null, "content", "logs").hasAdminKey()).isFalse();
        assertThat(new GhostProperties("url", " ", "content", "logs").hasAdminKey()).isFalse();
    }

    @Test
    void hasContentKey_ShouldReturnTrue_WhenValidKeyProvided() {
        GhostProperties properties = new GhostProperties("http://ghost.io", "admin", "content-key", "logs");
        assertThat(properties.hasContentKey()).isTrue();
    }

    @Test
    void hasContentKey_ShouldReturnFalse_WhenDefaultPlaceholder() {
        GhostProperties properties = new GhostProperties("http://ghost.io", "admin", "${GHOST_CONTENT_API_KEY}", "logs");
        assertThat(properties.hasContentKey()).isFalse();
    }
}
