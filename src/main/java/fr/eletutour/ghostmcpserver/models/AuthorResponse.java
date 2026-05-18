package fr.eletutour.ghostmcpserver.models;

import java.util.List;

public record AuthorResponse(
        List<Author> authors,
        Meta meta
) {
    public record Meta(Pagination pagination) {}
}
