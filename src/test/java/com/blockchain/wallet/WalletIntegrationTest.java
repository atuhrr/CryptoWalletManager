package com.blockchain.wallet;
import com.blockchain.wallet.dto.AssetDTO;
import com.blockchain.wallet.dto.WalletCreateDTO;
import com.blockchain.wallet.dto.WalletResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

@Testcontainers
@SpringBootTest(classes = CryptoWalletManagerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WalletIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("cryptowallet")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateWalletIntegration() {
        WalletCreateDTO walletCreateDTO = new WalletCreateDTO("user9integration1@example.com");
        ResponseEntity<WalletResponseDTO> response = restTemplate.postForEntity("/wallet/create", walletCreateDTO, WalletResponseDTO.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("user9integration1@example.com", response.getBody().email());
    }

    @Test
    void testAddAssetIntegration() {
        WalletCreateDTO walletCreateDTO = new WalletCreateDTO("user10@email.com");
        restTemplate.postForEntity("/wallet/create", walletCreateDTO, WalletResponseDTO.class);

        AssetDTO assetDTO = new AssetDTO("btc", new BigDecimal("50000"), new BigDecimal("0.5"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AssetDTO> requestEntity = new HttpEntity<>(assetDTO, headers);
        ResponseEntity<WalletResponseDTO> response = restTemplate.exchange("/wallet/add-asset?email=user10@email.com", HttpMethod.POST, requestEntity, WalletResponseDTO.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().assets().isEmpty());
    }

    @Test
    void testGetWalletIntegration() {
        WalletCreateDTO walletCreateDTO = new WalletCreateDTO("user10@email.com");
        restTemplate.postForEntity("/wallet/create", walletCreateDTO, WalletResponseDTO.class);

        ResponseEntity<WalletResponseDTO> response = restTemplate.getForEntity("/wallet/user10@email.com", WalletResponseDTO.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("user10@email.com", response.getBody().email());
    }
}
