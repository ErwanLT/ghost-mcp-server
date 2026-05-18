package fr.eletutour.ghostmcpserver.client;

import fr.eletutour.ghostmcpserver.configuration.GhostProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

public class GhostContentApiClient {

    private static final Logger log = LoggerFactory.getLogger(GhostContentApiClient.class);

    private final WebClient webClient;
    private final String ghostContentApiKey;

    public GhostContentApiClient(GhostProperties ghostProperties) {
        this.webClient = WebClient.builder()
                .baseUrl(ghostProperties.url() + "/ghost/api/content")
                .defaultHeader("Accept-Version", "v5.0")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024))
                .build();
        this.ghostContentApiKey = ghostProperties.contentApiKey();
    }

    public String getAllGhosts() {
        log.info("Fetching all recent posts from Ghost API");
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/posts/")
                        .queryParam("key", ghostContentApiKey)
                        .queryParam("limit", "100")
                        .queryParam("include", "tags,primary_tag")
                        .queryParam("fields", "id,title,status,slug")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


}
