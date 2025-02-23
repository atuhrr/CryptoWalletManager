package com.blockchain.wallet;

import com.blockchain.wallet.client.CoinCapFeignClient;
import com.blockchain.wallet.dto.AssetDTO;
import com.blockchain.wallet.dto.CoinCapResponse;
import com.blockchain.wallet.dto.WalletCreateDTO;
import com.blockchain.wallet.dto.WalletResponseDTO;
import com.blockchain.wallet.exception.TokenNotFoundException;
import com.blockchain.wallet.exception.WalletAlreadyExistsException;
import com.blockchain.wallet.mapper.AssetMapper;
import com.blockchain.wallet.mapper.WalletMapper;
import com.blockchain.wallet.model.Wallet;
import com.blockchain.wallet.repository.WalletRepository;
import com.blockchain.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private AssetMapper assetMapper;

    @Mock
    private CoinCapFeignClient coinCapFeignClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateWalletSuccess() {
        WalletCreateDTO dto = new WalletCreateDTO("test_user@example.com");
        when(walletRepository.existsByEmail(dto.email())).thenReturn(false);

        Wallet wallet = Wallet.builder()
                .id(1L)
                .email(dto.email())
                .assets(new ArrayList<>())
                .build();
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(walletMapper.toDto(wallet))
                .thenReturn(new WalletResponseDTO(dto.email(), new ArrayList<>(), BigDecimal.ZERO));

        WalletResponseDTO response = walletService.createWallet(dto);
        assertNotNull(response);
        assertEquals("test_user@example.com", response.email());
    }

    @Test
    void testCreateWalletAlreadyExists() {
        WalletCreateDTO dto = new WalletCreateDTO("test_user@example.com");
        when(walletRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(WalletAlreadyExistsException.class, () -> walletService.createWallet(dto));
    }

    @Test
    void testAddAssetNotFound() {
        String email = "not_found@example.com";
        AssetDTO assetDTO = new AssetDTO("ethereum", BigDecimal.valueOf(50000), BigDecimal.valueOf(0.5));

        when(walletRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> walletService.addAsset(email, assetDTO));
    }

    @Test
    void testAddAssetCoinCapNotFound() {
        String email = "test_user2@example.com";
        AssetDTO assetDTO = new AssetDTO("xyz", BigDecimal.valueOf(50000), BigDecimal.valueOf(0.5));

        Wallet wallet = Wallet.builder()
                .id(1L)
                .email(email)
                .assets(new ArrayList<>())
                .build();
        when(walletRepository.findByEmail(email)).thenReturn(Optional.of(wallet));
        // Simula que a resposta da API não possui dados válidos
        when(coinCapFeignClient.getAsset("xyz")).thenReturn(new CoinCapResponse(null));

        assertThrows(TokenNotFoundException.class, () -> walletService.addAsset(email, assetDTO));
    }

    @Test
    void testGetWalletByEmailSuccess() {
        String email = "test_user3@example.com";
        Wallet wallet = Wallet.builder()
                .id(1L)
                .email(email)
                .assets(new ArrayList<>())
                .build();
        when(walletRepository.findByEmailWithAssets(email)).thenReturn(Optional.of(wallet));
        when(walletMapper.toDto(wallet))
                .thenReturn(new WalletResponseDTO(email, new ArrayList<>(), BigDecimal.ZERO));

        WalletResponseDTO response = walletService.getWalletByEmail(email);
        assertNotNull(response);
        assertEquals(email, response.email());
    }
}
