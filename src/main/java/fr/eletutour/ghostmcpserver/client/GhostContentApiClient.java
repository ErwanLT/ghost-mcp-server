package fr.eletutour.ghostmcpserver.client;

import fr.eletutour.ghostmcpserver.configuration.GhostProperties;
import fr.eletutour.ghostmcpserver.models.AuthorResponse;
import fr.eletutour.ghostmcpserver.models.PostResponse;
import fr.eletutour.ghostmcpserver.models.TagResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GhostContentApiClient {

    private static final Logger log = LoggerFactory.getLogger(GhostContentApiClient.class);

    private final WebClient webClient;
    private final String ghostContentApiKey;
    private final Integer limit = 100;

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

    public PostResponse getPosts(int page) {
        log.info("Fetching posts from Ghost Content API, page {}", page);
        String operation = "fetch content posts page %d".formatted(page);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/posts/")
                                        .queryParam("key", ghostContentApiKey)
                                        .queryParam("limit", limit)
                                        .queryParam("page", page)
                                        .queryParam("fields", "id,title,slug,status,published_at,url")
                                        .queryParam("include", "tags,authors")
                                        .build()),
                        "Ghost Content API",
                        operation)
                        .bodyToMono(PostResponse.class),
                "Ghost Content API",
                operation
        );
    }

    public PostResponse getPostById(String id) {
        log.info("Fetching post with id '{}' from Ghost Content API", id);
        String operation = "fetch content post by id '%s'".formatted(id);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/posts/{id}/")
                                        .queryParam("key", ghostContentApiKey)
                                        .queryParam("include", "tags,authors")
                                        .build(id)),
                        "Ghost Content API",
                        operation)
                        .bodyToMono(PostResponse.class),
                "Ghost Content API",
                operation
        );
    }

    public PostResponse getPostBySlug(String slug) {
        log.info("Fetching post with slug '{}' from Ghost Content API", slug);
        String operation = "fetch content post by slug '%s'".formatted(slug);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/posts/slug/{slug}/")
                                        .queryParam("key", ghostContentApiKey)
                                        .queryParam("include", "tags,authors")
                                        .build(slug)),
                        "Ghost Content API",
                        operation)
                        .bodyToMono(PostResponse.class),
                "Ghost Content API",
                operation
        );
    }

    public AuthorResponse getAuthors(int page) {
        log.info("Fetching authors from Ghost Content API, page {}", page);
        String operation = "fetch authors page %d".formatted(page);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/authors/")
                                        .queryParam("key", ghostContentApiKey)
                                        .queryParam("limit", limit)
                                        .queryParam("page", page)
                                        .queryParam("include", "count.posts")
                                        .build()),
                        "Ghost Content API",
                        operation)
                        .bodyToMono(AuthorResponse.class),
                "Ghost Content API",
                operation
        );
    }

    public AuthorResponse getAuthorById(String id) {
        log.info("Fetching author with id '{}' from Ghost Content API", id);
        String operation = "fetch author by id '%s'".formatted(id);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/authors/{id}/")
                                        .queryParam("key", ghostContentApiKey)
                                        .queryParam("include", "count.posts")
                                        .build(id)),
                        "Ghost Content API",
                        operation)
                        .bodyToMono(AuthorResponse.class),
                "Ghost Content API",
                operation
        );
    }

    public AuthorResponse getAuthorBySlug(String slug) {
        log.info("Fetching author with slug '{}' from Ghost Content API", slug);
        String operation = "fetch author by slug '%s'".formatted(slug);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/authors/slug/{slug}/")
                                        .queryParam("key", ghostContentApiKey)
                                        .queryParam("include", "count.posts")
                                        .build(slug)),
                        "Ghost Content API",
                        operation)
                        .bodyToMono(AuthorResponse.class),
                "Ghost Content API",
                operation
        );
    }

    public TagResponse getTags(int page) {
        log.info("Fetching tags from Ghost Content API, page {}", page);
        String operation = "fetch tags page %d".formatted(page);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/tags/")
                                        .queryParam("key", ghostContentApiKey)
                                        .queryParam("limit", limit)
                                        .queryParam("page", page)
                                        .queryParam("include", "count.posts")
                                        .build()),
                        "Ghost Content API",
                        operation)
                        .bodyToMono(TagResponse.class),
                "Ghost Content API",
                operation
        );
    }

    public TagResponse getTagById(String id) {
        log.info("Fetching tag with id '{}' from Ghost Content API", id);
        String operation = "fetch tag by id '%s'".formatted(id);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/tags/{id}/")
                                        .queryParam("key", ghostContentApiKey)
                                        .queryParam("include", "count.posts")
                                        .build(id)),
                        "Ghost Content API",
                        operation)
                        .bodyToMono(TagResponse.class),
                "Ghost Content API",
                operation
        );
    }

    public TagResponse getTagBySlug(String slug) {
        log.info("Fetching tag with slug '{}' from Ghost Content API", slug);
        String operation = "fetch tag by slug '%s'".formatted(slug);
        return GhostApiClientSupport.execute(
                GhostApiClientSupport.retrieve(webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/tags/slug/{slug}/")
                                        .queryParam("key", ghostContentApiKey)
                                        .queryParam("include", "count.posts")
                                        .build(slug)),
                        "Ghost Content API",
                        operation)
                        .bodyToMono(TagResponse.class),
                "Ghost Content API",
                operation
        );
    }
}
