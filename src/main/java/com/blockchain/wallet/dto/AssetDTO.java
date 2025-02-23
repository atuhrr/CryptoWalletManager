package com.blockchain.wallet.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AssetDTO(
        @NotNull(message = "Symbol is required") String symbol,
        @NotNull(message = "Price is required") BigDecimal price,
        @NotNull(message = "Quantity is required") BigDecimal quantity
) {}
