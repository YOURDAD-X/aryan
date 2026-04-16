package com.ats.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ats_scores")
@Data
@NoArgsConstructor
public class AtsScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    private Double scorePercentage;
    private Double plagiarismScore;

    @Column(columnDefinition = "TEXT")
    private String matchedKeywords;

    @Column(columnDefinition = "TEXT")
    private String missingKeywords;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    private LocalDateTime calculationTime = LocalDateTime.now();
}
