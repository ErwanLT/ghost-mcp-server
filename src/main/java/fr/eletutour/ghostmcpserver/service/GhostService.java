package fr.eletutour.ghostmcpserver.service;

import fr.eletutour.ghostmcpserver.client.GhostAdminApiClient;
import fr.eletutour.ghostmcpserver.client.GhostContentApiClient;
import fr.eletutour.ghostmcpserver.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GhostService {

    private static final Logger log = LoggerFactory.getLogger(GhostService.class);
    private static final List<String> ALLOWED_STATUSES = List.of("draft", "published", "scheduled");

    private final GhostAdminApiClient adminApiClient;
    private final GhostContentApiClient contentApiClient;

    public GhostService(GhostAdminApiClient adminApiClient, GhostContentApiClient contentApiClient) {
        this.adminApiClient = adminApiClient;
        this.contentApiClient = contentApiClient;
    }

    // --- Content API Methods ---

    public List<Post> getAllContentPosts() {
        log.info("Fetching all posts from Content API");
        List<Post> allPosts = new ArrayList<>();
        int currentPage = 1;
        PostResponse response;
        do {
            response = contentApiClient.getPosts(currentPage);
            if (response != null && response.posts() != null) {
                allPosts.addAll(response.posts());
                currentPage++;
            }
        } while (response != null && response.meta().pagination().next() != null);
        return allPosts;
    }

    public Post getContentPostById(String id) {
        PostResponse response = contentApiClient.getPostById(id);
        return (response != null && !response.posts().isEmpty()) ? response.posts().get(0) : null;
    }

    public Post getContentPostBySlug(String slug) {
        PostResponse response = contentApiClient.getPostBySlug(slug);
        return (response != null && !response.posts().isEmpty()) ? response.posts().get(0) : null;
    }

    public List<Author> getAllAuthors() {
        log.info("Fetching all authors from Content API");
        List<Author> allAuthors = new ArrayList<>();
        int currentPage = 1;
        AuthorResponse response;
        do {
            response = contentApiClient.getAuthors(currentPage);
            if (response != null && response.authors() != null) {
                allAuthors.addAll(response.authors());
                currentPage++;
            }
        } while (response != null && response.meta().pagination().next() != null);
        return allAuthors;
    }

    public Author getAuthorById(String id) {
        AuthorResponse response = contentApiClient.getAuthorById(id);
        return (response != null && !response.authors().isEmpty()) ? response.authors().get(0) : null;
    }

    public Author getAuthorBySlug(String slug) {
        AuthorResponse response = contentApiClient.getAuthorBySlug(slug);
        return (response != null && !response.authors().isEmpty()) ? response.authors().get(0) : null;
    }

    public List<Tag> getAllTags() {
        log.info("Fetching all tags from Content API");
        List<Tag> allTags = new ArrayList<>();
        int currentPage = 1;
        TagResponse response;
        do {
            response = contentApiClient.getTags(currentPage);
            if (response != null && response.tags() != null) {
                allTags.addAll(response.tags());
                currentPage++;
            }
        } while (response != null && response.meta().pagination().next() != null);
        return allTags;
    }

    public Tag getTagById(String id) {
        TagResponse response = contentApiClient.getTagById(id);
        return (response != null && !response.tags().isEmpty()) ? response.tags().get(0) : null;
    }

    public Tag getTagBySlug(String slug) {
        TagResponse response = contentApiClient.getTagBySlug(slug);
        return (response != null && !response.tags().isEmpty()) ? response.tags().get(0) : null;
    }

    // --- Admin API Methods ---

    public List<Post> getAllAdminPosts() {
        log.info("Fetching all posts from Admin API");
        List<Post> allPosts = new ArrayList<>();
        int currentPage = 1;
        PostResponse response;
        do {
            response = adminApiClient.getPosts(currentPage);
            if (response != null && response.posts() != null) {
                allPosts.addAll(response.posts());
                currentPage++;
            }
        } while (response != null && response.meta().pagination().next() != null);
        return allPosts;
    }

    public List<Post> getAdminPostsByAuthor(String author) {
        log.info("Fetching all posts for author {} from Admin API", author);
        List<Post> allPosts = new ArrayList<>();
        int currentPage = 1;
        PostResponse response;
        do {
            response = adminApiClient.getPostsByAuthor(author, currentPage);
            if (response != null && response.posts() != null) {
                allPosts.addAll(response.posts());
                currentPage++;
            }
        } while (response != null && response.meta().pagination().next() != null);
        log.info("Find {} post for author {} from Admin API", allPosts.size(), author);
        return allPosts;
    }

    public List<Post> getAdminPostsByTag(String tag) {
        log.info("Fetching all posts for tag {} from Admin API", tag);
        List<Post> allPosts = new ArrayList<>();
        int currentPage = 1;
        PostResponse response;
        do {
            response = adminApiClient.getPostsByTag(tag, currentPage);
            if (response != null && response.posts() != null) {
                allPosts.addAll(response.posts());
                currentPage++;
            }
        } while (response != null && response.meta().pagination().next() != null);
        return allPosts;
    }

    public Post getAdminPostBySlug(String slug) {
        PostResponse response = adminApiClient.getPostBySlug(slug);
        return (response != null && !response.posts().isEmpty()) ? response.posts().get(0) : null;
    }

    public Post createAdminPost(PostInput post) {
        if (post.title() == null || post.title().isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire pour créer un article.");
        }
        PostInput toCreate = post.status() == null
                ? new PostInput(post.title(), post.html(), "draft", post.tags(), post.authors(), post.customExcerpt(),
                post.featureImage(), post.featured(), post.metaTitle(), post.metaDescription(),
                post.publishedAt(), null)
                : post;
        validateStatus(toCreate);
        PostResponse response = adminApiClient.createPost(toCreate.withUpdatedAt(null));
        return (response != null && !response.posts().isEmpty()) ? response.posts().get(0) : null;
    }

    public Post updateAdminPost(String id, PostInput changes) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'id de l'article est obligatoire pour une mise à jour.");
        }
        if (isEmpty(changes)) {
            throw new IllegalArgumentException("Aucun champ à mettre à jour n'a été fourni.");
        }
        if (changes.title() != null && changes.title().isBlank()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide.");
        }
        validateStatus(changes);

        // Ghost exige le updated_at courant pour détecter les modifications concurrentes
        PostResponse current = adminApiClient.getPostById(id);
        if (current == null || current.posts() == null || current.posts().isEmpty()) {
            throw new IllegalArgumentException("Aucun article trouvé pour l'id '%s'.".formatted(id));
        }
        OffsetDateTime updatedAt = current.posts().get(0).updatedAt();

        PostResponse response = adminApiClient.updatePost(id, changes.withUpdatedAt(updatedAt));
        return (response != null && !response.posts().isEmpty()) ? response.posts().get(0) : null;
    }

    private static void validateStatus(PostInput post) {
        if (post.status() == null) {
            return;
        }
        if (!ALLOWED_STATUSES.contains(post.status())) {
            throw new IllegalArgumentException("Statut '%s' invalide. Valeurs possibles : %s."
                    .formatted(post.status(), String.join(", ", ALLOWED_STATUSES)));
        }
        if ("scheduled".equals(post.status()) && post.publishedAt() == null) {
            throw new IllegalArgumentException("Une date de publication (publishedAt) est obligatoire pour planifier un article.");
        }
    }

    private static boolean isEmpty(PostInput post) {
        return post == null || (post.title() == null && post.html() == null && post.status() == null
                && post.tags() == null && post.authors() == null && post.customExcerpt() == null && post.featureImage() == null
                && post.featured() == null && post.metaTitle() == null && post.metaDescription() == null
                && post.publishedAt() == null);
    }
}
