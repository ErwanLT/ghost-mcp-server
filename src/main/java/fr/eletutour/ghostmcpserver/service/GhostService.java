package fr.eletutour.ghostmcpserver.service;

import fr.eletutour.ghostmcpserver.client.GhostAdminApiClient;
import fr.eletutour.ghostmcpserver.client.GhostContentApiClient;
import fr.eletutour.ghostmcpserver.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GhostService {

    private static final Logger log = LoggerFactory.getLogger(GhostService.class);

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
}
