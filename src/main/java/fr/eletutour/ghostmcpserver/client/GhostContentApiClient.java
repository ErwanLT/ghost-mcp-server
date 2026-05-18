package fr.eletutour.ghostmcpserver.client;

import fr.eletutour.ghostmcpserver.configuration.GhostProperties;
import fr.eletutour.ghostmcpserver.models.AuthorResponse;
import fr.eletutour.ghostmcpserver.models.PostResponse;
import fr.eletutour.ghostmcpserver.models.TagResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/posts/")
                        .queryParam("key", ghostContentApiKey)
                        .queryParam("limit", limit)
                        .queryParam("page", page)
                        .queryParam("fields", "id,title,slug,status,published_at,url")
                        .queryParam("include", "tags,authors")
                        .build())
                .retrieve()
                .bodyToMono(PostResponse.class)
                .block();
    }

    public PostResponse getPostById(String id) {
        log.info("Fetching post with id '{}' from Ghost Content API", id);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/posts/" + id + "/")
                        .queryParam("key", ghostContentApiKey)
                        .queryParam("include", "tags,authors")
                        .build())
                .retrieve()
                .bodyToMono(PostResponse.class)
                .block();
    }

    public PostResponse getPostBySlug(String slug) {
        log.info("Fetching post with slug '{}' from Ghost Content API", slug);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/posts/slug/" + slug + "/")
                        .queryParam("key", ghostContentApiKey)
                        .queryParam("include", "tags,authors")
                        .build())
                .retrieve()
                .bodyToMono(PostResponse.class)
                .block();
    }

    public AuthorResponse getAuthors(int page) {
        log.info("Fetching authors from Ghost Content API, page {}", page);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/authors/")
                        .queryParam("key", ghostContentApiKey)
                        .queryParam("limit", limit)
                        .queryParam("page", page)
                        .queryParam("include", "count.posts")
                        .build())
                .retrieve()
                .bodyToMono(AuthorResponse.class)
                .block();
    }

    public AuthorResponse getAuthorById(String id) {
        log.info("Fetching author with id '{}' from Ghost Content API", id);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/authors/" + id + "/")
                        .queryParam("key", ghostContentApiKey)
                        .queryParam("include", "count.posts")
                        .build())
                .retrieve()
                .bodyToMono(AuthorResponse.class)
                .block();
    }

    public AuthorResponse getAuthorBySlug(String slug) {
        log.info("Fetching author with slug '{}' from Ghost Content API", slug);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/authors/slug/" + slug + "/")
                        .queryParam("key", ghostContentApiKey)
                        .queryParam("include", "count.posts")
                        .build())
                .retrieve()
                .bodyToMono(AuthorResponse.class)
                .block();
    }

    public TagResponse getTags(int page) {
        log.info("Fetching tags from Ghost Content API, page {}", page);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tags/")
                        .queryParam("key", ghostContentApiKey)
                        .queryParam("limit", limit)
                        .queryParam("page", page)
                        .queryParam("include", "count.posts")
                        .build())
                .retrieve()
                .bodyToMono(TagResponse.class)
                .block();
    }

    public TagResponse getTagById(String id) {
        log.info("Fetching tag with id '{}' from Ghost Content API", id);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tags/" + id + "/")
                        .queryParam("key", ghostContentApiKey)
                        .queryParam("include", "count.posts")
                        .build())
                .retrieve()
                .bodyToMono(TagResponse.class)
                .block();
    }

    public TagResponse getTagBySlug(String slug) {
        log.info("Fetching tag with slug '{}' from Ghost Content API", slug);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tags/slug/" + slug + "/")
                        .queryParam("key", ghostContentApiKey)
                        .queryParam("include", "count.posts")
                        .build())
                .retrieve()
                .bodyToMono(TagResponse.class)
                .block();
    }
}
