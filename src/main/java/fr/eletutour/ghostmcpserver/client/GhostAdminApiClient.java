package fr.eletutour.ghostmcpserver.client;

import fr.eletutour.ghostmcpserver.configuration.GhostProperties;
import fr.eletutour.ghostmcpserver.models.PostResponse;
import fr.eletutour.ghostmcpserver.service.GhostJwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GhostAdminApiClient {

    private static final Logger log = LoggerFactory.getLogger(GhostAdminApiClient.class);

    private final WebClient webClient;
    private final GhostJwtService jwtService;
    private final Integer limit = 100;

    public GhostAdminApiClient(GhostProperties ghostProperties,
                               GhostJwtService jwtService) {
        this.jwtService = jwtService;
        this.webClient = WebClient.builder()
                .baseUrl(ghostProperties.url() + "/ghost/api/admin")
                .defaultHeader("Accept-Version", "v5.0")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    public PostResponse getPosts(int page) {
        log.info("Fetching posts from Ghost Admin API, page {}", page);
        String operation = "fetch admin posts page %d".formatted(page);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/posts/")
                                        .queryParam("limit", limit)
                                        .queryParam("page", page)
                                        .queryParam("fields", "id,title,slug,status,published_at,url")
                                        .queryParam("include", "tags,authors")
                                        .build())
                                .header("Authorization", "Ghost " + jwtService.generateToken()),
                        "Ghost Admin API",
                        operation)
                        .bodyToMono(PostResponse.class),
                "Ghost Admin API",
                operation
        );
    }

    public PostResponse getPostsByAuthor(String author, int page) {
        log.info("Fetching posts for author '{}' from Ghost Admin API, page {}", author, page);
        String operation = "fetch admin posts for author '%s' page %d".formatted(author, page);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/posts/")
                                        .queryParam("filter", "authors:" + author)
                                        .queryParam("limit", limit)
                                        .queryParam("page", page)
                                        .queryParam("fields", "id,title,slug,status,published_at,url")
                                        .queryParam("include", "tags,authors")
                                        .build())
                                .header("Authorization", "Ghost " + jwtService.generateToken()),
                        "Ghost Admin API",
                        operation)
                        .bodyToMono(PostResponse.class),
                "Ghost Admin API",
                operation
        );
    }

    public PostResponse getPostsByTag(String tag, int page) {
        log.info("Fetching posts for tag '{}' from Ghost Admin API, page {}", tag, page);
        String operation = "fetch admin posts for tag '%s' page %d".formatted(tag, page);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/posts/")
                                        .queryParam("filter", "tag:" + tag)
                                        .queryParam("limit", limit)
                                        .queryParam("page", page)
                                        .queryParam("fields", "id,title,slug,status,published_at,url")
                                        .queryParam("include", "tags,authors")
                                        .build())
                                .header("Authorization", "Ghost " + jwtService.generateToken()),
                        "Ghost Admin API",
                        operation)
                        .bodyToMono(PostResponse.class),
                "Ghost Admin API",
                operation
        );
    }

    public PostResponse getPostBySlug(String slug) {
        log.info("Fetching post for slug '{}' from Ghost Admin API", slug);
        String operation = "fetch admin post by slug '%s'".formatted(slug);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/posts/slug/{slug}/")
                                        .queryParam("formats", "html,plaintext")
                                        .queryParam("include", "tags,authors")
                                        .build(slug))
                                .header("Authorization", "Ghost " + jwtService.generateToken()),
                        "Ghost Admin API",
                        operation)
                        .bodyToMono(PostResponse.class),
                "Ghost Admin API",
                operation
        );
    }
}
