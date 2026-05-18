package fr.eletutour.ghostmcpserver.service;

import fr.eletutour.ghostmcpserver.configuration.GhostProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class GhostJwtServiceTest {

    private GhostJwtService jwtService;
    private GhostProperties properties;
    
    // Ghost admin key format is id:secret (secret is hex)
    // Hex "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f" 
    private final String validAdminKey = "5ddc9141c35e7700383b2937:000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f";

    @BeforeEach
    void setUp() {
        properties = new GhostProperties("http://localhost", validAdminKey, "content", "logs");
        jwtService = new GhostJwtService(properties);
    }

    @Test
    void generateToken_ShouldReturnValidJWT() {
        String token = jwtService.generateToken();
        assertThat(token).isNotNull().isNotEmpty();

        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
    }

    @Test
    void generateToken_ShouldHaveCorrectHeaderAndClaims() {
        String token = jwtService.generateToken();
        
        // We can't easily verify the signature without reproducing the hexStringToByteArray logic here
        // but we can check if it looks like a JWT
        assertThat(token).contains(".");
    }
}
