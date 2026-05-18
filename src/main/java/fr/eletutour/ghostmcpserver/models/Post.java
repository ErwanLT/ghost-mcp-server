package fr.eletutour.ghostmcpserver.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record Post(
        String slug,
        String id,
        String uuid,
        String title,
        String html,

        @JsonProperty("comment_id")
        String commentId,

        @JsonProperty("feature_image")
        String featureImage,

        @JsonProperty("feature_image_alt")
        String featureImageAlt,

        @JsonProperty("feature_image_caption")
        String featureImageCaption,

        boolean featured,

        @JsonProperty("meta_title")
        String metaTitle,

        @JsonProperty("meta_description")
        String metaDescription,

        @JsonProperty("created_at")
        OffsetDateTime createdAt,

        @JsonProperty("updated_at")
        OffsetDateTime updatedAt,

        @JsonProperty("published_at")
        OffsetDateTime publishedAt,

        @JsonProperty("custom_excerpt")
        String customExcerpt,

        @JsonProperty("codeinjection_head")
        String codeinjectionHead,

        @JsonProperty("codeinjection_foot")
        String codeinjectionFoot,

        @JsonProperty("og_image")
        String ogImage,

        @JsonProperty("og_title")
        String ogTitle,

        @JsonProperty("og_description")
        String ogDescription,

        @JsonProperty("twitter_image")
        String twitterImage,

        @JsonProperty("twitter_title")
        String twitterTitle,

        @JsonProperty("twitter_description")
        String twitterDescription,

        @JsonProperty("custom_template")
        String customTemplate,

        @JsonProperty("canonical_url")
        String canonicalUrl,

        List<Author> authors,
        List<Tag> tags,

        @JsonProperty("primary_author")
        Author primaryAuthor,

        @JsonProperty("primary_tag")
        Tag primaryTag,

        String url,
        String excerpt
) {
}