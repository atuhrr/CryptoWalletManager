package com.blockchain.wallet.service;

import com.blockchain.wallet.client.CoinCapFeignClient;
import com.blockchain.wallet.dto.*;
import com.blockchain.wallet.exception.TokenNotFoundException;
import com.blockchain.wallet.exception.WalletAlreadyExistsException;
import com.blockchain.wallet.mapper.AssetMapper;
import com.blockchain.wallet.mapper.WalletMapper;
import com.blockchain.wallet.model.Asset;
import com.blockchain.wallet.model.Wallet;
import com.blockchain.wallet.repository.WalletRepository;
import com.blockchain.wallet.util.AssetEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final AssetMapper assetMapper;
    private final CoinCapFeignClient coinCapFeignClient;

    @Transactional
    public WalletResponseDTO createWallet(WalletCreateDTO walletCreateDTO) {
        if (walletRepository.existsByEmail(walletCreateDTO.email())) {
            throw new WalletAlreadyExistsException("Wallet already exists for email: " + walletCreateDTO.email());
        }
        Wallet wallet = Wallet.builder()
                .email(walletCreateDTO.email())
                .assets(new ArrayList<>())
                .build();
        wallet = walletRepository.save(wallet);
        return walletMapper.toDto(wallet);
    }

    @Transactional
    public WalletResponseDTO addAsset(String email, AssetDTO assetDTO) {
        Wallet wallet = walletRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found for email: " + email));

        var response = coinCapFeignClient.getAsset(assetDTO.symbol().toLowerCase());
        if (response == null || response.data() == null || response.data().priceUsd() == null) {
            throw new TokenNotFoundException("Token not found: " + assetDTO.symbol());
        }
        BigDecimal marketPrice = new BigDecimal(response.data().priceUsd());

        Asset asset = Asset.builder()
                .symbol(assetDTO.symbol())
                .price(marketPrice)
                .quantity(assetDTO.quantity())
                .wallet(wallet)
                .build();
        wallet.getAssets().add(asset);
        wallet = walletRepository.save(wallet);
        return walletMapper.toDto(wallet);
    }

    @Transactional(readOnly = true)
    public WalletResponseDTO getWalletByEmail(String email) {
        Wallet wallet = walletRepository.findByEmailWithAssets(email)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found for email: " + email));
        return walletMapper.toDto(wallet);
    }

    @Transactional(readOnly = true)
    public WalletEvaluationDTO evaluateWallet(String email, String date) {
        Wallet wallet = walletRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found for email: " + email));

        List<CompletableFuture<AssetEvaluation>> futures = wallet.getAssets().stream()
                .map(asset -> CompletableFuture.supplyAsync(() -> {
                    var historicalResponse = coinCapFeignClient.getAsset(asset.getSymbol().toLowerCase());
                    if (historicalResponse == null || historicalResponse.data() == null || historicalResponse.data().priceUsd() == null) {
                        throw new TokenNotFoundException("Historical price not found for token: " + asset.getSymbol());
                    }
                    BigDecimal historicalPrice = new BigDecimal(historicalResponse.data().priceUsd());
                    BigDecimal currentValue = asset.getPrice().multiply(asset.getQuantity());
                    BigDecimal historicalValue = historicalPrice.multiply(asset.getQuantity());
                    BigDecimal percentageChange = (historicalValue.compareTo(BigDecimal.ZERO) == 0) ?
                            BigDecimal.ZERO :
                            currentValue.subtract(historicalValue)
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(historicalValue, 2, BigDecimal.ROUND_HALF_UP);
                    return new AssetEvaluation(asset.getSymbol(), historicalValue, currentValue, percentageChange);
                })).toList();

        var evaluations = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        BigDecimal totalCurrent = evaluations.stream()
                .map(AssetEvaluation::currentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AssetEvaluation bestAsset = evaluations.stream()
                .max(Comparator.comparing(AssetEvaluation::percentageChange))
                .orElse(null);

        AssetEvaluation worstAsset = evaluations.stream()
                .min(Comparator.comparing(AssetEvaluation::percentageChange))
                .orElse(null);

        return new WalletEvaluationDTO(
                totalCurrent,
                bestAsset != null ? new AssetPerformanceDTO(bestAsset.symbol(), bestAsset.percentageChange()) : null,
                worstAsset != null ? new AssetPerformanceDTO(worstAsset.symbol(), worstAsset.percentageChange()) : null
        );
    }
}
