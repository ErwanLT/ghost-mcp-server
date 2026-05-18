package fr.eletutour.ghostmcpserver.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ghost")
public record GhostProperties(
        String url,
        String adminApiKey,
        String contentApiKey,
        String logFile) {

    public boolean hasAdminKey() {
        return adminApiKey != null && !adminApiKey.isBlank() && !adminApiKey.equals("${GHOST_ADMIN_API_KEY}");
    }

    public boolean hasContentKey() {
        return contentApiKey != null && !contentApiKey.isBlank() && !contentApiKey.equals("${GHOST_CONTENT_API_KEY}");
    }
}