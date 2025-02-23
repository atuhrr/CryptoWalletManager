package com.blockchain.wallet.dto;

import java.math.BigDecimal;

public record WalletEvaluationDTO(
        BigDecimal totalValue,
        AssetPerformanceDTO bestAsset,
        AssetPerformanceDTO worstAsset
) {}
