package fr.eletutour.ghostmcpserver.configuration;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ghost")
public record GhostProperties(
        @NotBlank(message = "Ghost URL is required")
        @Pattern(regexp = "https?://.+", message = "Ghost URL must start with http:// or https://")
        String url,

        String adminApiKey,

        String contentApiKey,

        @NotBlank(message = "Ghost log file path is required")
        String logFile) {

    private static final String ADMIN_KEY_PATTERN = "^[^:]+:[0-9a-fA-F]+$";

    public GhostProperties {
        url = trimToNull(removeTrailingSlash(url));
        adminApiKey = trimToNull(adminApiKey);
        contentApiKey = trimToNull(contentApiKey);
        logFile = trimToNull(logFile);
    }

    public boolean hasAdminKey() {
        return adminApiKey != null && !adminApiKey.isBlank() && !adminApiKey.equals("${GHOST_ADMIN_API_KEY}");
    }

    public boolean hasContentKey() {
        return contentApiKey != null && !contentApiKey.isBlank() && !contentApiKey.equals("${GHOST_CONTENT_API_KEY}");
    }

    @AssertTrue(message = "At least one Ghost API key must be configured")
    public boolean isAtLeastOneApiKeyConfigured() {
        return hasAdminKey() || hasContentKey();
    }

    @AssertTrue(message = "Ghost Admin API key must use the format id:hexSecret")
    public boolean isAdminApiKeyFormatValid() {
        if (!hasAdminKey()) {
            return true;
        }

        String[] parts = adminApiKey.split(":", -1);
        return parts.length == 2
                && adminApiKey.matches(ADMIN_KEY_PATTERN)
                && parts[1].length() % 2 == 0;
    }

    private static String removeTrailingSlash(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        while (trimmed.endsWith("/") && trimmed.length() > 1) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
