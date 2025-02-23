package com.blockchain.wallet.util;

import java.math.BigDecimal;

public record AssetEvaluation(
        String symbol,
        BigDecimal historicalValue,
        BigDecimal currentValue,
        BigDecimal percentageChange
) {}
