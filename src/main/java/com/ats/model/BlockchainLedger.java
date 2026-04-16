package com.ats.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "blockchain_ledger")
@Data
@NoArgsConstructor
public class BlockchainLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false, unique = true)
    private String fileHash;

    @Column(nullable = false)
    private String previousHash;

    private LocalDateTime timestamp = LocalDateTime.now();
}
