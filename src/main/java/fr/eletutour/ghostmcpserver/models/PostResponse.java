package fr.eletutour.ghostmcpserver.models;

import java.util.List;

public record PostResponse(
        List<Post> posts,
        Meta meta
) {
    public record Meta(Pagination pagination) {}
}
