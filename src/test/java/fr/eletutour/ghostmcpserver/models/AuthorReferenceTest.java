package fr.eletutour.ghostmcpserver.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorReferenceTest {

    @Test
    void of_ShouldUseEmail_WhenIdentifierContainsAt() {
        assertThat(AuthorReference.of(" erwan@example.com "))
                .isEqualTo(new AuthorReference(null, "erwan@example.com"));
    }

    @Test
    void of_ShouldUseId_Otherwise() {
        assertThat(AuthorReference.of("5c739b7c8a59a6c8ddc164a1"))
                .isEqualTo(new AuthorReference("5c739b7c8a59a6c8ddc164a1", null));
    }

    @Test
    void of_ShouldRejectBlankIdentifier() {
        assertThatThrownBy(() -> AuthorReference.of(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
