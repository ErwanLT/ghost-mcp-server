package fr.eletutour.ghostmcpserver.tools;

import fr.eletutour.ghostmcpserver.service.GhostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GhostContentToolsTest {

    @Mock
    private GhostService ghostService;

    @InjectMocks
    private GhostContentTools contentTools;

    @Test
    void getPosts_ShouldCallService() {
        contentTools.getPosts();
        verify(ghostService).getAllContentPosts();
    }

    @Test
    void getPostById_ShouldCallService() {
        contentTools.getPostById("id");
        verify(ghostService).getContentPostById("id");
    }

    @Test
    void getAuthors_ShouldCallService() {
        contentTools.getAuthors();
        verify(ghostService).getAllAuthors();
    }

    @Test
    void getTags_ShouldCallService() {
        contentTools.getTags();
        verify(ghostService).getAllTags();
    }
}
