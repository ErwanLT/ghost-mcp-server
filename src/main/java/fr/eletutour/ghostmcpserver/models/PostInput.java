package fr.eletutour.ghostmcpserver.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Champs modifiables d'un post envoyés à l'API Admin (création ou mise à jour partielle).
 * Les champs null ne sont pas sérialisés afin de ne pas écraser les valeurs existantes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostInput(
        String title,
        String html,
        String status,
        List<String> tags,

        @JsonProperty("custom_excerpt")
        String customExcerpt,

        @JsonProperty("feature_image")
        String featureImage,

        Boolean featured,

        @JsonProperty("meta_title")
        String metaTitle,

        @JsonProperty("meta_description")
        String metaDescription,

        @JsonProperty("published_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        OffsetDateTime publishedAt,

        @JsonProperty("updated_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        OffsetDateTime updatedAt
) {
    public PostInput withUpdatedAt(OffsetDateTime updatedAt) {
        return new PostInput(title, html, status, tags, customExcerpt, featureImage, featured,
                metaTitle, metaDescription, publishedAt, updatedAt);
    }
}
