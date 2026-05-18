package fr.eletutour.ghostmcpserver.tools;

import fr.eletutour.ghostmcpserver.models.Post;
import fr.eletutour.ghostmcpserver.service.GhostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GhostAdminToolsTest {

    @Mock
    private GhostService ghostService;

    @InjectMocks
    private GhostAdminTools adminTools;

    @Test
    void getAdminPosts_ShouldCallService() {
        List<Post> posts = List.of();
        when(ghostService.getAllAdminPosts()).thenReturn(posts);

        List<Post> result = adminTools.getAdminPosts();

        assertThat(result).isEqualTo(posts);
        verify(ghostService).getAllAdminPosts();
    }

    @Test
    void findAdminPostsByAuthor_ShouldCallService() {
        adminTools.getAdminPostsByAuthor("erwan");
        verify(ghostService).getAdminPostsByAuthor("erwan");
    }
}
