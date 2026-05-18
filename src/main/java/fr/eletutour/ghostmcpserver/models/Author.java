package fr.eletutour.ghostmcpserver.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Author(
        String id,
        String name,
        String slug,

        @JsonProperty("profile_image")
        String profileImage,

        @JsonProperty("cover_image")
        String coverImage,

        String bio,
        String website,
        String location,
        String facebook,
        String twitter,

        @JsonProperty("meta_title")
        String metaTitle,

        @JsonProperty("meta_description")
        String metaDescription,

        String url
) {
}