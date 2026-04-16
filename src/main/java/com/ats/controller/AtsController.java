package com.ats.controller;

import com.ats.dto.AtsScoreRequest;
import com.ats.model.AtsScore;
import com.ats.service.AtsScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ats")
@CrossOrigin(origins = "*")
public class AtsController {

    @Autowired
    private AtsScoringService atsScoringService;

    @PostMapping("/score")
    public ResponseEntity<?> scoreResume(@RequestBody AtsScoreRequest request) {
        try {
            AtsScore score = atsScoringService.processAtsScore(request.getResumeId(), request.getJobDescription());
            return ResponseEntity.ok(score);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
