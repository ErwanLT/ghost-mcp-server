package fr.eletutour.ghostmcpserver.models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Référence vers un auteur Ghost existant, identifié par son email ou son id.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthorReference(String id, String email) {

    public static AuthorReference of(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Un auteur doit être identifié par son email ou son id.");
        }
        String value = identifier.trim();
        return value.contains("@") ? new AuthorReference(null, value) : new AuthorReference(value, null);
    }
}
