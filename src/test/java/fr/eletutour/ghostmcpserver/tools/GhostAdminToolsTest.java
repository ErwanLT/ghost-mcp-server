package fr.eletutour.ghostmcpserver.tools;

import fr.eletutour.ghostmcpserver.models.AuthorReference;
import fr.eletutour.ghostmcpserver.models.Post;
import fr.eletutour.ghostmcpserver.models.PostInput;
import fr.eletutour.ghostmcpserver.service.GhostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

    @Test
    void createAdminPost_ShouldMapParametersToInput() {
        adminTools.createAdminPost("Titre", "<p>x</p>", "scheduled", List.of("Java"),
                List.of("erwan@example.com", "5c739b7c8a59a6c8ddc164a1"), "extrait",
                null, true, null, null, "2026-10-10T09:00:00Z");

        verify(ghostService).createAdminPost(new PostInput("Titre", "<p>x</p>", "scheduled", List.of("Java"),
                List.of(new AuthorReference(null, "erwan@example.com"), new AuthorReference("5c739b7c8a59a6c8ddc164a1", null)),
                "extrait", null, true, null, null, OffsetDateTime.parse("2026-10-10T09:00:00Z"), null));
    }

    @Test
    void updateAdminPost_ShouldMapParametersToInput() {
        adminTools.updateAdminPost("abc", "Nouveau", null, null, null, null, null, null, null, null, null, null);

        verify(ghostService).updateAdminPost(eq("abc"), eq(new PostInput("Nouveau", null, null, null, null,
                null, null, null, null, null, null, null)));
    }

    @Test
    void createAdminPost_ShouldRejectInvalidDate() {
        assertThatThrownBy(() -> adminTools.createAdminPost("Titre", null, "scheduled", null, null, null,
                null, null, null, null, "demain"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("demain");
        verifyNoInteractions(ghostService);
    }
}
