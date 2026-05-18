package fr.eletutour.ghostmcpserver.client;

import fr.eletutour.ghostmcpserver.configuration.GhostProperties;
import fr.eletutour.ghostmcpserver.models.PostResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

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
}
