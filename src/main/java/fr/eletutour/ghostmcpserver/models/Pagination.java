package fr.eletutour.ghostmcpserver.models;

public record Pagination(
        int page,
        int limit,
        int pages,
        int total,
        Integer next,
        Integer prev
) {
}
