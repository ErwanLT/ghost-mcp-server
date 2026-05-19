package fr.eletutour.ghostmcpserver.client;

import fr.eletutour.ghostmcpserver.configuration.GhostProperties;
import fr.eletutour.ghostmcpserver.models.PostResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import okhttp3.mockwebserver.RecordedRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GhostContentApiClientTest {

    private MockWebServer mockWebServer;
    private GhostContentApiClient apiClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        GhostProperties properties = new GhostProperties(
                mockWebServer.url("/").toString(),
                "admin",
                "content",
                "logs"
        );
        apiClient = new GhostContentApiClient(properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getPosts_ShouldDeserializeCorrectly() throws IOException {
        String json = StreamUtils.copyToString(
                new ClassPathResource("json/content/posts_page1.json").getInputStream(),
                StandardCharsets.UTF_8
        );

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        PostResponse response = apiClient.getPosts(1);

        assertThat(response).isNotNull();
        assertThat(response.posts()).hasSize(1);
        assertThat(response.posts().get(0).title()).isEqualTo("Post 1");
        assertThat(response.meta().pagination().total()).isEqualTo(2);
        assertThat(response.meta().pagination().next()).isEqualTo(2);
    }

    @Test
    void getPostById_ShouldDeserializeFullPost() throws IOException {
        String json = StreamUtils.copyToString(
                new ClassPathResource("json/content/post_by_id.json").getInputStream(),
                StandardCharsets.UTF_8
        );

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        PostResponse response = apiClient.getPostById("1");

        assertThat(response.posts()).hasSize(1);
        assertThat(response.posts().get(0).html()).isEqualTo("<p>Content</p>");
        assertThat(response.posts().get(0).authors()).hasSize(1);
        assertThat(response.posts().get(0).authors().get(0).name()).isEqualTo("Author 1");
    }

    @Test
    void getPostBySlug_ShouldEncodeSlugPathSegment() throws IOException, InterruptedException {
        String json = StreamUtils.copyToString(
                new ClassPathResource("json/content/post_by_id.json").getInputStream(),
                StandardCharsets.UTF_8
        );

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        apiClient.getPostBySlug("ghost tips/advanced");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath())
                .startsWith("/ghost/api/content/posts/slug/ghost%20tips%2Fadvanced/");
    }

    @Test
    void getPostById_ShouldThrowReadableException_WhenPostNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"errors\":[{\"message\":\"Resource not found\"}]}")
                .addHeader("Content-Type", "application/json"));

        assertThatThrownBy(() -> apiClient.getPostById("missing"))
                .isInstanceOf(GhostApiException.class)
                .hasMessage("Ghost Content API could not find the requested resource while trying to fetch content post by id 'missing'.")
                .extracting("statusCode", "responseBody")
                .containsExactly(404, "{\"errors\":[{\"message\":\"Resource not found\"}]}");
    }

    @Test
    void getPosts_ShouldThrowReadableException_WhenRateLimited() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(429)
                .setBody("{\"errors\":[{\"message\":\"Rate limit exceeded\"}]}")
                .addHeader("Content-Type", "application/json"));

        assertThatThrownBy(() -> apiClient.getPosts(1))
                .isInstanceOf(GhostApiException.class)
                .hasMessage("Ghost Content API rate limit was reached while trying to fetch content posts page 1. Retry later.")
                .extracting("statusCode")
                .isEqualTo(429);
    }

    @Test
    void getPosts_ShouldThrowReadableException_WhenResponseIsTooLargeForClient() {
        String body = "{\"posts\":[{\"html\":\"" + "x".repeat(2 * 1024 * 1024) + "\"}]}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        assertThatThrownBy(() -> apiClient.getPosts(1))
                .isInstanceOf(GhostApiException.class)
                .hasMessage("Ghost Content API returned a response that is too large while trying to fetch content posts page 1");
    }
}
