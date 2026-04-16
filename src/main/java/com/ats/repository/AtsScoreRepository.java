package com.ats.repository;

import com.ats.model.AtsScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtsScoreRepository extends JpaRepository<AtsScore, Long> {
    List<AtsScore> findByResumeId(Long resumeId);
}
