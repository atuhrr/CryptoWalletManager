package com.blockchain.wallet.dto;

import java.math.BigDecimal;

public record AssetPerformanceDTO(String symbol, BigDecimal percentageChange) {}
