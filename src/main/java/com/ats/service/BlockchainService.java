package com.ats.service;

import com.ats.model.BlockchainLedger;
import com.ats.model.Resume;
import com.ats.repository.BlockchainLedgerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class BlockchainService {

    @Autowired
    private BlockchainLedgerRepository ledgerRepository;

    public BlockchainLedger addBlock(Resume resume, String fileContent) {
        String previousHash = "0"; // Genesis block default
        Optional<BlockchainLedger> lastBlock = ledgerRepository.findTopByOrderByTimestampDesc();
        if (lastBlock.isPresent()) {
            previousHash = lastBlock.get().getFileHash();
        }

        String dataToHash = previousHash + resume.getId() + fileContent;
        String newHash = calculateSHA256(dataToHash);

        BlockchainLedger newLedgerEntry = new BlockchainLedger();
        newLedgerEntry.setResume(resume);
        newLedgerEntry.setPreviousHash(previousHash);
        newLedgerEntry.setFileHash(newHash);

        return ledgerRepository.save(newLedgerEntry);
    }

    private String calculateSHA256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Algorithm not found", e);
        }
    }
}
