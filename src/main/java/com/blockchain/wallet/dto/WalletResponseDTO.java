package com.blockchain.wallet.dto;

import java.math.BigDecimal;
import java.util.List;

public record WalletResponseDTO(
        String email,
        List<AssetDTO> assets,
        BigDecimal totalValue
) {}
