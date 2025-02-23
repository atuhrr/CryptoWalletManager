package com.blockchain.wallet.service;

import com.blockchain.wallet.client.CoinCapFeignClient;
import com.blockchain.wallet.dto.CoinCapResponse;
import com.blockchain.wallet.model.Asset;
import com.blockchain.wallet.model.Wallet;
import com.blockchain.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceUpdateScheduler {

    private final WalletRepository walletRepository;
    private final CoinCapFeignClient coinCapFeignClient;

    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    @Scheduled(fixedRateString = "60000")
    public void updatePrices() {
        log.info("Starting price update...");
        List<Wallet> wallets = walletRepository.findAllWithAssets();

        List<Asset> allAssets = wallets.stream()
                .flatMap(wallet -> wallet.getAssets().stream())
                .distinct()
                .toList();

        List<Callable<Void>> tasks = allAssets.stream().map(asset -> (Callable<Void>) () -> {
            try {
                CoinCapResponse response = coinCapFeignClient.getAsset(asset.getSymbol().toLowerCase());
                if (response != null && response.data() != null && response.data().priceUsd() != null) {
                    BigDecimal latestPrice = new BigDecimal(response.data().priceUsd());
                    asset.setPrice(latestPrice);
                    log.info("Updated price: {} -> ${}", asset.getSymbol(), latestPrice);
                } else {
                    log.warn("Price not found for {}", asset.getSymbol());
                }
            } catch (Exception e) {
                log.error("Error updating price for {}: {}", asset.getSymbol(), e.getMessage());
            }
            return null;
        }).toList();

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error("Error executing price updates", e);
        }

        walletRepository.saveAll(wallets);
        log.info("Price update complete.");
    }
}

