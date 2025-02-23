package com.blockchain.wallet.dto;

public record CoinCapResponse(CoinCapData data) {
    public record CoinCapData(String priceUsd) {}
}
