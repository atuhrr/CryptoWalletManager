package com.blockchain.wallet.mapper;

import com.blockchain.wallet.dto.WalletResponseDTO;
import com.blockchain.wallet.model.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = AssetMapper.class)
public interface WalletMapper {

    @Mapping(target = "totalValue", expression = "java(calculateTotalValue(wallet))")
    WalletResponseDTO toDto(Wallet wallet);

    default BigDecimal calculateTotalValue(Wallet wallet) {
        return wallet.getAssets().stream()
                .map(asset -> asset.getPrice().multiply(asset.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
