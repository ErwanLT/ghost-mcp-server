package fr.eletutour.ghostmcpserver.tools;

import fr.eletutour.ghostmcpserver.models.AuthorReference;
import fr.eletutour.ghostmcpserver.models.Post;
import fr.eletutour.ghostmcpserver.models.PostInput;
import fr.eletutour.ghostmcpserver.service.GhostService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
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

    @Tool(
            name = "createAdminPost",
            description = """
            Crée un nouvel article via l'API Admin de Ghost.
            Le contenu est fourni en HTML et converti par Ghost dans son format éditeur (Lexical).
            Par défaut l'article est créé en brouillon (draft) : ne publier que si l'utilisateur le demande explicitement.
            Les tags sont référencés par leur nom ; un tag inexistant est créé automatiquement.
            Les auteurs sont référencés par leur email ou leur id ; sans auteur, Ghost attribue l'article au propriétaire du blog.
            Retourne l'article créé (avec son id, son slug et son updated_at).
            """
    )
    public Post createAdminPost(
            @ToolParam(description = "Titre de l'article") String title,
            @ToolParam(required = false, description = "Contenu de l'article en HTML") String html,
            @ToolParam(required = false, description = "Statut : draft (défaut), published ou scheduled") String status,
            @ToolParam(required = false, description = "Noms des tags à associer à l'article") List<String> tags,
            @ToolParam(required = false, description = "Auteurs de l'article, identifiés par email ou par id") List<String> authors,
            @ToolParam(required = false, description = "Extrait personnalisé (résumé court)") String customExcerpt,
            @ToolParam(required = false, description = "URL de l'image mise en avant") String featureImage,
            @ToolParam(required = false, description = "Article mis en avant (featured)") Boolean featured,
            @ToolParam(required = false, description = "Titre SEO (meta title)") String metaTitle,
            @ToolParam(required = false, description = "Description SEO (meta description)") String metaDescription,
            @ToolParam(required = false, description = "Date de publication ISO-8601 (ex. 2026-10-06T09:00:00Z), obligatoire si status=scheduled") String publishedAt) {
        return ghostService.createAdminPost(new PostInput(title, html, status, tags, toAuthorReferences(authors), customExcerpt, featureImage,
                featured, metaTitle, metaDescription, parseDate(publishedAt), null));
    }

    @Tool(
            name = "updateAdminPost",
            description = """
            Met à jour un article existant via l'API Admin de Ghost, à partir de son id.
            Seuls les champs fournis sont modifiés ; les autres restent inchangés.
            Attention : les listes de tags et d'auteurs fournies REMPLACENT entièrement les valeurs existantes.
            Pour ajouter un tag ou un auteur, récupérer d'abord l'article et renvoyer la liste complète.
            Le contenu HTML fourni remplace l'intégralité du contenu de l'article.
            Retourne l'article mis à jour.
            """
    )
    public Post updateAdminPost(
            @ToolParam(description = "Id de l'article à modifier") String id,
            @ToolParam(required = false, description = "Nouveau titre") String title,
            @ToolParam(required = false, description = "Nouveau contenu complet en HTML") String html,
            @ToolParam(required = false, description = "Nouveau statut : draft, published ou scheduled") String status,
            @ToolParam(required = false, description = "Liste complète des noms de tags (remplace les tags existants)") List<String> tags,
            @ToolParam(required = false, description = "Liste complète des auteurs, par email ou id (remplace les auteurs existants)") List<String> authors,
            @ToolParam(required = false, description = "Extrait personnalisé (résumé court)") String customExcerpt,
            @ToolParam(required = false, description = "URL de l'image mise en avant") String featureImage,
            @ToolParam(required = false, description = "Article mis en avant (featured)") Boolean featured,
            @ToolParam(required = false, description = "Titre SEO (meta title)") String metaTitle,
            @ToolParam(required = false, description = "Description SEO (meta description)") String metaDescription,
            @ToolParam(required = false, description = "Date de publication ISO-8601 (ex. 2026-10-06T09:00:00Z), obligatoire si status=scheduled") String publishedAt) {
        return ghostService.updateAdminPost(id, new PostInput(title, html, status, tags, toAuthorReferences(authors), customExcerpt, featureImage,
                featured, metaTitle, metaDescription, parseDate(publishedAt), null));
    }

    private static List<AuthorReference> toAuthorReferences(List<String> authors) {
        return authors == null ? null : authors.stream().map(AuthorReference::of).toList();
    }

    private static OffsetDateTime parseDate(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Date '%s' invalide : format ISO-8601 attendu (ex. 2026-10-06T09:00:00Z).".formatted(date), e);
        }
    }
}
