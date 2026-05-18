package fr.eletutour.ghostmcpserver.service;

import fr.eletutour.ghostmcpserver.client.GhostAdminApiClient;
import fr.eletutour.ghostmcpserver.client.GhostContentApiClient;
import fr.eletutour.ghostmcpserver.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GhostServiceTest {

    @Mock
    private GhostAdminApiClient adminApiClient;

    @Mock
    private GhostContentApiClient contentApiClient;

    private GhostService ghostService;

    @BeforeEach
    void setUp() {
        ghostService = new GhostService(adminApiClient, contentApiClient);
    }

    @Test
    void getAllContentPosts_ShouldFetchAllPages() {
        // Page 1
        Pagination p1 = new Pagination(1, 1, 2, 2, 2, null);
        Post post1 = mock(Post.class);
        PostResponse resp1 = new PostResponse(List.of(post1), new PostResponse.Meta(p1));

        // Page 2
        Pagination p2 = new Pagination(2, 1, 2, 2, null, 1);
        Post post2 = mock(Post.class);
        PostResponse resp2 = new PostResponse(List.of(post2), new PostResponse.Meta(p2));

        when(contentApiClient.getPosts(1)).thenReturn(resp1);
        when(contentApiClient.getPosts(2)).thenReturn(resp2);

        List<Post> result = ghostService.getAllContentPosts();

        assertThat(result).hasSize(2).containsExactly(post1, post2);
        verify(contentApiClient, times(2)).getPosts(anyInt());
    }

    @Test
    void getContentPostById_ShouldReturnPost() {
        Post post = mock(Post.class);
        PostResponse resp = new PostResponse(List.of(post), null);
        when(contentApiClient.getPostById("id")).thenReturn(resp);

        Post result = ghostService.getContentPostById("id");

        assertThat(result).isEqualTo(post);
    }

    @Test
    void getAllAuthors_ShouldFetchAllPages() {
        Pagination p1 = new Pagination(1, 1, 1, 1, null, null);
        Author author = mock(Author.class);
        AuthorResponse resp = new AuthorResponse(List.of(author), new AuthorResponse.Meta(p1));

        when(contentApiClient.getAuthors(1)).thenReturn(resp);

        List<Author> result = ghostService.getAllAuthors();

        assertThat(result).hasSize(1).containsExactly(author);
        verify(contentApiClient, times(1)).getAuthors(1);
    }

    @Test
    void getAllTags_ShouldFetchAllPages() {
        Pagination p1 = new Pagination(1, 1, 1, 1, null, null);
        Tag tag = mock(Tag.class);
        TagResponse resp = new TagResponse(List.of(tag), new TagResponse.Meta(p1));

        when(contentApiClient.getTags(1)).thenReturn(resp);

        List<Tag> result = ghostService.getAllTags();

        assertThat(result).hasSize(1).containsExactly(tag);
        verify(contentApiClient, times(1)).getTags(1);
    }

    @Test
    void getAdminPostsByAuthor_ShouldFetchAllPages() {
        Pagination p1 = new Pagination(1, 1, 1, 1, null, null);
        Post post = mock(Post.class);
        PostResponse resp = new PostResponse(List.of(post), new PostResponse.Meta(p1));

        when(adminApiClient.getPostsByAuthor(eq("erwan"), anyInt())).thenReturn(resp);

        List<Post> result = ghostService.getAdminPostsByAuthor("erwan");

        assertThat(result).hasSize(1).containsExactly(post);
        verify(adminApiClient).getPostsByAuthor("erwan", 1);
    }

    @Test
    void getAllContentPosts_ShouldHandleNullResponseGracefully() {
        when(contentApiClient.getPosts(1)).thenReturn(null);

        List<Post> result = ghostService.getAllContentPosts();

        assertThat(result).isEmpty();
        verify(contentApiClient, times(1)).getPosts(1);
    }
}
