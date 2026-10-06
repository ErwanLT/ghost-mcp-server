package fr.eletutour.ghostmcpserver.client;

import fr.eletutour.ghostmcpserver.configuration.GhostProperties;
import fr.eletutour.ghostmcpserver.models.PostInput;
import fr.eletutour.ghostmcpserver.models.PostResponse;
import fr.eletutour.ghostmcpserver.service.GhostJwtService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GhostAdminApiClientTest {

    private MockWebServer mockWebServer;
    private GhostAdminApiClient apiClient;
    private GhostJwtService jwtService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        jwtService = mock(GhostJwtService.class);
        when(jwtService.generateToken()).thenReturn("mock-token");

        GhostProperties properties = new GhostProperties(
                mockWebServer.url("/").toString(),
                "admin:secret",
                "content",
                "logs"
        );
        apiClient = new GhostAdminApiClient(properties, jwtService);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getPosts_ShouldSendAuthHeaderAndDeserialize() throws IOException, InterruptedException {
        String json = StreamUtils.copyToString(
                new ClassPathResource("json/content/posts_page1.json").getInputStream(),
                StandardCharsets.UTF_8
        );

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        PostResponse response = apiClient.getPosts(1);

        assertThat(response).isNotNull();
        assertThat(mockWebServer.takeRequest().getHeader("Authorization")).isEqualTo("Ghost mock-token");
        assertThat(response.posts().get(0).title()).isEqualTo("Post 1");
    }

    @Test
    void getPosts_ShouldThrowReadableException_WhenAuthenticationFails() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"errors\":[{\"message\":\"Invalid token\"}]}")
                .addHeader("Content-Type", "application/json"));

        assertThatThrownBy(() -> apiClient.getPosts(1))
                .isInstanceOf(GhostApiException.class)
                .hasMessage("Ghost Admin API authentication failed while trying to fetch admin posts page 1. Check the configured Ghost API key.")
                .extracting("statusCode", "responseBody")
                .containsExactly(401, "{\"errors\":[{\"message\":\"Invalid token\"}]}");
    }

    @Test
    void getPostBySlug_ShouldEncodeSlugPathSegment() throws IOException, InterruptedException {
        String json = StreamUtils.copyToString(
                new ClassPathResource("json/content/posts_page1.json").getInputStream(),
                StandardCharsets.UTF_8
        );

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        apiClient.getPostBySlug("draft notes/2026");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath())
                .startsWith("/ghost/api/admin/posts/slug/draft%20notes%2F2026/");
    }

    @Test
    void createPost_ShouldPostHtmlSourceWithOnlyProvidedFields() throws IOException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody(loadPostsJson())
                .addHeader("Content-Type", "application/json"));

        PostInput input = new PostInput("Mon article", "<p>Contenu</p>", "draft", List.of("Java", "Spring"),
                null, null, null, null, null, null, null);
        PostResponse response = apiClient.createPost(input);

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).startsWith("/ghost/api/admin/posts/?").contains("source=html");
        assertThat(request.getHeader("Authorization")).isEqualTo("Ghost mock-token");
        assertThat(request.getBody().readUtf8()).isEqualTo(
                "{\"posts\":[{\"title\":\"Mon article\",\"html\":\"<p>Contenu</p>\",\"status\":\"draft\",\"tags\":[\"Java\",\"Spring\"]}]}");
        assertThat(response.posts()).isNotEmpty();
    }

    @Test
    void updatePost_ShouldPutUpdatedAtAndSkipHtmlSourceWhenNoHtml() throws IOException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody(loadPostsJson())
                .addHeader("Content-Type", "application/json"));

        PostInput input = new PostInput("Nouveau titre", null, null, null, null, null, null, null, null, null,
                OffsetDateTime.parse("2026-10-01T10:00:00Z"));
        apiClient.updatePost("abc123", input);

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("PUT");
        assertThat(request.getPath()).startsWith("/ghost/api/admin/posts/abc123/").doesNotContain("source=html");
        assertThat(request.getBody().readUtf8())
                .contains("\"title\":\"Nouveau titre\"")
                .contains("\"updated_at\":\"2026-10-01T10:00:00Z\"")
                .doesNotContain("\"html\"");
    }

    @Test
    void updatePost_ShouldUseHtmlSourceWhenHtmlProvided() throws IOException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody(loadPostsJson())
                .addHeader("Content-Type", "application/json"));

        PostInput input = new PostInput(null, "<p>Nouveau</p>", null, null, null, null, null, null, null, null,
                OffsetDateTime.parse("2026-10-01T10:00:00Z"));
        apiClient.updatePost("abc123", input);

        assertThat(mockWebServer.takeRequest().getPath()).contains("source=html");
    }

    @Test
    void updatePost_ShouldThrowReadableException_WhenConflict() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(409)
                .setBody("{\"errors\":[{\"type\":\"UpdateCollisionError\"}]}")
                .addHeader("Content-Type", "application/json"));

        PostInput input = new PostInput("Titre", null, null, null, null, null, null, null, null, null,
                OffsetDateTime.parse("2026-10-01T10:00:00Z"));

        assertThatThrownBy(() -> apiClient.updatePost("abc123", input))
                .isInstanceOf(GhostApiException.class)
                .hasMessage("Ghost Admin API returned HTTP 409 while trying to update admin post 'abc123'.");
    }

    private String loadPostsJson() throws IOException {
        return StreamUtils.copyToString(
                new ClassPathResource("json/content/posts_page1.json").getInputStream(),
                StandardCharsets.UTF_8
        );
    }
}
