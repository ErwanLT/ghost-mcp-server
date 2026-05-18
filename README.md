# Ghost MCP Server

Un serveur MCP (Model Context Protocol) pour interroger les API Content et Admin de Ghost.io.

## Fonctionnalités

- **API Content** : Récupération des articles publics, auteurs et tags.
- **API Admin** : Accès aux articles (incluant brouillons et planifiés), filtrage par auteur et tag.
- **Pagination** : Gestion automatique de la pagination pour récupérer l'intégralité des données.
- **Optimisation** : Filtrage des champs pour les listes et inclusion systématique des auteurs et tags.
- **Configuration Dynamique** : Les outils sont exposés en fonction des clés API fournies.

## Installation

### Prérequis

- Java 25
- Maven (pour le build)

### Build

```bash
mvn clean package
```

## Configuration Claude

Pour utiliser ce serveur avec Claude Desktop, ajoutez la configuration suivante dans votre fichier `claude_desktop_config.json` :

```json
{
  "mcpServers": {
    "ghost-mcp-server": {
      "command": "java",
      "args": [
        "-Dio.netty.noUnsafe=true",
        "--enable-native-access=ALL-UNNAMED",
        "-jar",
        "[CHEMIN_VERS_VOTRE_PROJET]/target/ghost-mcp-server-0.0.1-SNAPSHOT.jar"
      ],
      "env": {
        "GHOST_URL": "https://votre-site.ghost.io",
        "GHOST_ADMIN_API_KEY": "VOTRE_ADMIN_API_KEY",
        "GHOST_CONTENT_API_KEY": "VOTRE_CONTENT_API_KEY",
        "GHOST_LOG_FILE": "[CHEMIN_VERS_VOTRE_PROJET]/src/logs/mcp-server.log"
      }
    }
  }
}
```

### Variables d'environnement

- `GHOST_URL` : L'URL de votre instance Ghost (ex: `https://mon-blog.ghost.io`).
- `GHOST_ADMIN_API_KEY` : Votre clé API Admin (facultatif, active les outils Admin).
- `GHOST_CONTENT_API_KEY` : Votre clé API Content (facultatif, active les outils Content).
- `GHOST_LOG_FILE` : Chemin vers le fichier de log (facultatif, défaut: `src/logs/mcp-server.log`).

## Outils disponibles

### Content API (Activé si `GHOST_CONTENT_API_KEY` est présente)
- `getAllContentPosts` : Récupère tous les articles publics.
- `getContentPostById` : Détails d'un article par ID.
- `getContentPostBySlug` : Détails d'un article par slug.
- `getAllAuthors` : Liste des auteurs.
- `getAllTags` : Liste des tags.

### Admin API (Activé si `GHOST_ADMIN_API_KEY` est présente)
- `getAllAdminPosts` : Récupère tous les articles (publiés et brouillons).
- `findAdminPostsByAuthor` : Articles d'un auteur spécifique.
- `findAdminPostsByTag` : Articles associés à un tag.
- `getAdminPostBySlug` : Détails administratifs d'un article.
