package fr.eletutour.ghostmcpserver.configuration;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GhostPropertiesTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

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

    @Test
    void constructor_ShouldNormalizeWhitespaceAndTrailingSlash() {
        GhostProperties properties = new GhostProperties(" https://ghost.io/ ", " admin ", " content ", " logs ");

        assertThat(properties.url()).isEqualTo("https://ghost.io");
        assertThat(properties.adminApiKey()).isEqualTo("admin");
        assertThat(properties.contentApiKey()).isEqualTo("content");
        assertThat(properties.logFile()).isEqualTo("logs");
    }

    @Test
    void validation_ShouldPass_WhenContentApiKeyConfigured() {
        GhostProperties properties = new GhostProperties("https://ghost.io", null, "content-key", "logs");

        assertThat(validator.validate(properties)).isEmpty();
    }

    @Test
    void validation_ShouldPass_WhenValidAdminApiKeyConfigured() {
        GhostProperties properties = new GhostProperties(
                "https://ghost.io",
                "5ddc9141c35e7700383b2937:000102030405060708090a0b0c0d0e0f",
                null,
                "logs"
        );

        assertThat(validator.validate(properties)).isEmpty();
    }

    @Test
    void validation_ShouldRejectMissingApiKeys() {
        GhostProperties properties = new GhostProperties("https://ghost.io", null, null, "logs");

        assertThat(messagesFor(properties))
                .contains("At least one Ghost API key must be configured");
    }

    @Test
    void validation_ShouldRejectInvalidAdminApiKeyFormat() {
        GhostProperties properties = new GhostProperties("https://ghost.io", "id:not-hex", null, "logs");

        assertThat(messagesFor(properties))
                .contains("Ghost Admin API key must use the format id:hexSecret");
    }

    @Test
    void validation_ShouldRejectInvalidUrl() {
        GhostProperties properties = new GhostProperties("ghost.io", null, "content-key", "logs");

        assertThat(messagesFor(properties))
                .contains("Ghost URL must start with http:// or https://");
    }

    @Test
    void validation_ShouldRejectBlankLogFile() {
        GhostProperties properties = new GhostProperties("https://ghost.io", null, "content-key", " ");

        assertThat(messagesFor(properties))
                .contains("Ghost log file path is required");
    }

    private Set<String> messagesFor(GhostProperties properties) {
        return validator.validate(properties).stream()
                .map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());
    }
}
