package com.blockchain.wallet.repository;

import com.blockchain.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT w FROM Wallet w JOIN FETCH w.assets WHERE w.email = :email")
    Optional<Wallet> findByEmailWithAssets(@Param("email") String email);

    @Query("SELECT DISTINCT w FROM Wallet w LEFT JOIN FETCH w.assets")
    List<Wallet> findAllWithAssets();

}
