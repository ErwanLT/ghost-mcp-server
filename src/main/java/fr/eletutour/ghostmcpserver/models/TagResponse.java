package fr.eletutour.ghostmcpserver.models;

import java.util.List;

public record TagResponse(
        List<Tag> tags,
        Meta meta
) {
    public record Meta(Pagination pagination) {}
}
