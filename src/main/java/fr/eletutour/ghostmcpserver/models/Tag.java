package fr.eletutour.ghostmcpserver.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Tag(
        String id,
        String name,
        String slug,
        String description,

        @JsonProperty("feature_image")
        String featureImage,

        String visibility,

        @JsonProperty("meta_title")
        String metaTitle,

        @JsonProperty("meta_description")
        String metaDescription,

        String url
) {
}