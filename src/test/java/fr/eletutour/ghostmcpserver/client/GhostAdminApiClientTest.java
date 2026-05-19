package fr.eletutour.ghostmcpserver.client;

import fr.eletutour.ghostmcpserver.configuration.GhostProperties;
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
}
