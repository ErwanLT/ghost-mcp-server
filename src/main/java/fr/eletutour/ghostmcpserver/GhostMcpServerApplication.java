package fr.eletutour.ghostmcpserver;

import fr.eletutour.ghostmcpserver.configuration.GhostProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GhostProperties.class)
public class GhostMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GhostMcpServerApplication.class, args);
    }

}
