package fr.eletutour.ghostmcpserver.tools;

import fr.eletutour.ghostmcpserver.models.Post;
import fr.eletutour.ghostmcpserver.service.GhostService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GhostAdminTools {

    private final GhostService ghostService;

    public GhostAdminTools(GhostService ghostService) {
        this.ghostService = ghostService;
    }

    @Tool(
            name = "getAllAdminPosts",
            description = """
            Récupère tous les articles récents via l'API Admin de Ghost.
            Cela inclut tous les articles, y compris les brouillons (drafts) et les articles planifiés (scheduled).
            Retourne une liste contenant les objets Post complets.
            """
    )
    public List<Post> getAdminPosts() {
        return ghostService.getAllAdminPosts();
    }

    @Tool(
            name = "findAdminPostsByAuthor",
            description = """
            Recherche les articles de blog écrits par un auteur spécifique (par exemple 'erwan') via l'API Admin.
            Permet d'accéder aux articles (publiés ou non) d'un auteur particulier.
            Retourne une liste contenant les objets Post complets.
            """
    )
    public List<Post> getAdminPostsByAuthor(String author) {
        return ghostService.getAdminPostsByAuthor(author);
    }

    @Tool(
            name = "findAdminPostsByTag",
            description = """
            Recherche les articles associés à un tag spécifique via l'API Admin.
            Utile pour filtrer le contenu par catégorie taxonomique, y compris les articles non publiés.
            Retourne une liste d'objets Post complets.
            """
    )
    public List<Post> getAdminPostsByTag(String tag) {
        return ghostService.getAdminPostsByTag(tag);
    }

    @Tool(
            name = "getAdminPostBySlug",
            description = """
            Récupère les détails administratifs d'un article unique via son slug (URL friendly).
            Fournit les informations complètes, incluant les formats html et plaintext si disponibles.
            """
    )
    public Post getAdminPostBySlug(String slug) {
        return ghostService.getAdminPostBySlug(slug);
    }
}
