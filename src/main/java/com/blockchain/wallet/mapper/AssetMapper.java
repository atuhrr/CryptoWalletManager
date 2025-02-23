package com.blockchain.wallet.mapper;

import com.blockchain.wallet.dto.AssetDTO;
import com.blockchain.wallet.model.Asset;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssetMapper {
    AssetDTO toDto(Asset asset);
}
