package fr.eletutour.ghostmcpserver.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ghost")
public record GhostProperties(
        String url,
        String adminApiKey,
        String contentApiKey) {
}