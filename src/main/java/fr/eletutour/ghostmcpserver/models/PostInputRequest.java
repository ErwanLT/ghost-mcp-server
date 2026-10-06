package fr.eletutour.ghostmcpserver.models;

import java.util.List;

public record PostInputRequest(List<PostInput> posts) {

    public static PostInputRequest of(PostInput post) {
        return new PostInputRequest(List.of(post));
    }
}
