package fr.eletutour.ghostmcpserver.tools;

import fr.eletutour.ghostmcpserver.models.Author;
import fr.eletutour.ghostmcpserver.models.Post;
import fr.eletutour.ghostmcpserver.models.Tag;
import fr.eletutour.ghostmcpserver.service.GhostService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GhostContentTools {

    private final GhostService ghostService;

    public GhostContentTools(GhostService ghostService) {
        this.ghostService = ghostService;
    }

    @Tool(
            name = "getAllContentPosts",
            description = """
            Récupère tous les articles récents via l'API Content de Ghost.
            Retourne uniquement les articles publics et publiés avec leurs titres, slugs et métadonnées de base.
            """
    )
    public List<Post> getPosts() {
        return ghostService.getAllContentPosts();
    }

    @Tool(
            name = "getContentPostById",
            description = """
            Récupère un article unique par son identifiant via l'API Content.
            Utile pour obtenir le contenu complet d'un article spécifique quand son ID est connu.
            """
    )
    public Post getPostById(String id) {
        return ghostService.getContentPostById(id);
    }

    @Tool(
            name = "getContentPostBySlug",
            description = """
            Récupère un article unique par son slug (partie friendly de l'URL) via l'API Content.
            C'est la méthode recommandée pour afficher le contenu d'un article à partir de son lien.
            """
    )
    public Post getPostBySlug(String slug) {
        return ghostService.getContentPostBySlug(slug);
    }

    @Tool(
            name = "getAllAuthors",
            description = """
            Récupère la liste de tous les auteurs ayant publié des articles via l'API Content.
            Inclut les détails comme le nom, la bio et le nombre d'articles publiés.
            """
    )
    public List<Author> getAuthors() {
        return ghostService.getAllAuthors();
    }

    @Tool(
            name = "getAuthorById",
            description = """
            Récupère les détails d'un auteur spécifique par son identifiant unique via l'API Content.
            """
    )
    public Author getAuthorById(String id) {
        return ghostService.getAuthorById(id);
    }

    @Tool(
            name = "getAuthorBySlug",
            description = """
            Récupère les détails d'un auteur spécifique par son slug via l'API Content.
            """
    )
    public Author getAuthorBySlug(String slug) {
        return ghostService.getAuthorBySlug(slug);
    }

    @Tool(
            name = "getAllTags",
            description = """
            Récupère tous les tags utilisés sur le site via l'API Content.
            Retourne les noms des tags, leurs descriptions et le nombre d'articles associés.
            """
    )
    public List<Tag> getTags() {
        return ghostService.getAllTags();
    }

    @Tool(
            name = "getTagById",
            description = """
            Récupère les détails d'un tag spécifique par son identifiant unique via l'API Content.
            """
    )
    public Tag getTagById(String id) {
        return ghostService.getTagById(id);
    }

    @Tool(
            name = "getTagBySlug",
            description = """
            Récupère les détails d'un tag spécifique par son slug via l'API Content.
            """
    )
    public Tag getTagBySlug(String slug) {
        return ghostService.getTagBySlug(slug);
    }
}
