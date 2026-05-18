package fr.eletutour.ghostmcpserver.configuration;

import fr.eletutour.ghostmcpserver.tools.GhostAdminTools;
import fr.eletutour.ghostmcpserver.tools.GhostContentTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider ghostToolsProvider(GhostProperties ghostProperties,
                                                   GhostAdminTools ghostAdminTools,
                                                   GhostContentTools ghostContentTools) {
        MethodToolCallbackProvider.Builder builder = MethodToolCallbackProvider.builder();

        if (ghostProperties.hasAdminKey()) {
            builder.toolObjects(ghostAdminTools);
        }

        if (ghostProperties.hasContentKey()) {
            builder.toolObjects(ghostContentTools);
        }

        return builder.build();
    }
}