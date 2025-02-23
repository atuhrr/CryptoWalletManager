package com.blockchain.wallet.controller;

import com.blockchain.wallet.dto.*;
import com.blockchain.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @Operation(
            summary = "Create a new wallet",
            description = "Creates a new wallet linked to the provided email",
            tags = {"Wallets"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Wallet successfully created"),
            @ApiResponse(responseCode = "409", description = "A wallet already exists for this email")
    })
    @PostMapping("/create")
    public ResponseEntity<WalletResponseDTO> createWallet(
            @Valid @RequestBody WalletCreateDTO dto) {
        WalletResponseDTO wallet = walletService.createWallet(dto);
        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Add an asset to the wallet",
            description = "Allows adding a cryptocurrency asset to the user's wallet",
            tags = {"Assets"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset successfully added"),
            @ApiResponse(responseCode = "404", description = "Wallet or asset not found")
    })
    @PostMapping("/add-asset")
    public ResponseEntity<WalletResponseDTO> addAsset(
            @RequestParam String email,
            @Valid @RequestBody AssetDTO assetDTO) {
        WalletResponseDTO wallet = walletService.addAsset(email, assetDTO);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }

    @Operation(
            summary = "Get wallet details by email",
            description = "Returns the wallet's details and assets",
            tags = {"Wallets"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet found"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @GetMapping("/{email}")
    public ResponseEntity<WalletResponseDTO> getWallet(
            @PathVariable String email) {
        WalletResponseDTO wallet = walletService.getWalletByEmail(email);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }

    @Operation(
            summary = "Evaluate wallet performance",
            description = "Evaluates the portfolio by analyzing asset performance",
            tags = {"Evaluation"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluation completed successfully"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @GetMapping("/evaluate")
    public ResponseEntity<WalletEvaluationDTO> evaluateWallet(
            @RequestParam String email,
            @RequestParam String date) {
        WalletEvaluationDTO evaluation = walletService.evaluateWallet(email, date);
        return new ResponseEntity<>(evaluation, HttpStatus.OK);
    }
}
