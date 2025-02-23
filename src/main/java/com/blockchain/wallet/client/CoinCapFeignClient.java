package com.blockchain.wallet.client;

import com.blockchain.wallet.dto.CoinCapResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "coinCapClient", url = "https://api.coincap.io/v2",
        configuration = com.blockchain.wallet.config.FeignClientConfig.class)
public interface CoinCapFeignClient {
    @GetMapping("/assets/{id}")
    CoinCapResponse getAsset(@PathVariable("id") String id);
}
