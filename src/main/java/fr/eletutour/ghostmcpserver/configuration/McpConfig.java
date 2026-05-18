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
    public ToolCallbackProvider ghostToolsProvider(GhostAdminTools ghostAdminTools, GhostContentTools ghostContentTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(ghostAdminTools, ghostContentTools)
                .build();
    }
}