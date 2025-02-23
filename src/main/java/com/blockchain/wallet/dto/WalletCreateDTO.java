package com.blockchain.wallet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record WalletCreateDTO(
        @NotNull(message = "Email cannot be null")
        @Email(message = "Invalid email format") String email
) {}
