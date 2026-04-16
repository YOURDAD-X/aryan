package com.ats.repository;

import com.ats.model.BlockchainLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockchainLedgerRepository extends JpaRepository<BlockchainLedger, Long> {
    Optional<BlockchainLedger> findTopByOrderByTimestampDesc();
}
