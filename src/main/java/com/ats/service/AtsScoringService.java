package com.ats.service;

import com.ats.model.AtsScore;
import com.ats.model.Resume;
import com.ats.repository.AtsScoreRepository;
import com.ats.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class AtsScoringService {

    @Autowired
    private AtsScoreRepository atsScoreRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    public AtsScore processAtsScore(Long resumeId, String jobDescription) throws Exception {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        // Fork/Join Multithreading via CompletableFuture
        CompletableFuture<Map<String, Object>> keywordFuture = processKeywordsAsync(resume.getExtractedText(), jobDescription);
        CompletableFuture<Double> plagiarismFuture = checkPlagiarismAsync(resume);

        CompletableFuture.allOf(keywordFuture, plagiarismFuture).join();

        Map<String, Object> keywordResults = keywordFuture.get();
        Double plagiarismResult = plagiarismFuture.get();

        AtsScore score = new AtsScore();
        score.setResume(resume);
        score.setJobDescription(jobDescription);
        
        List<String> matched = (List<String>) keywordResults.get("matched");
        List<String> missing = (List<String>) keywordResults.get("missing");
        
        score.setMatchedKeywords(String.join(", ", matched));
        score.setMissingKeywords(String.join(", ", missing));
        
        double percentage = (double) matched.size() / (matched.size() + missing.size()) * 100.0;
        score.setScorePercentage(percentage > 0 ? percentage : 0.0);
        score.setPlagiarismScore(plagiarismResult);
        
        String suggestions = generateSuggestions(percentage, missing, plagiarismResult);
        score.setSuggestions(suggestions);

        return atsScoreRepository.save(score);
    }

    @Async("atsTaskExecutor")
    public CompletableFuture<Map<String, Object>> processKeywordsAsync(String resumeText, String jobDescription) {
        // Simple mock of keywords from JD
        String[] jdWords = jobDescription.toLowerCase().replaceAll("[^a-z\\s]", "").split("\\s+");
        Set<String> jdKeywords = Arrays.stream(jdWords).filter(w -> w.length() > 3).collect(Collectors.toSet());

        String rsmTextLower = resumeText.toLowerCase().replaceAll("[^a-z\\s]", "");
        
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String kw : jdKeywords) {
            if (rsmTextLower.contains(kw)) {
                matched.add(kw);
            } else {
                missing.add(kw);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("matched", matched);
        map.put("missing", missing);
        return CompletableFuture.completedFuture(map);
    }

    @Async("atsTaskExecutor")
    public CompletableFuture<Double> checkPlagiarismAsync(Resume currentResume) {
        List<Resume> allResumes = resumeRepository.findAll();
        double highestSimilarity = 0.0;

        for (Resume other : allResumes) {
            if (!other.getId().equals(currentResume.getId())) {
                double similarity = calculateJaccardSimilarity(currentResume.getExtractedText(), other.getExtractedText());
                if (similarity > highestSimilarity) {
                    highestSimilarity = similarity;
                }
            }
        }
        return CompletableFuture.completedFuture(highestSimilarity * 100.0); // as percentage
    }

    private double calculateJaccardSimilarity(String s1, String s2) {
        Set<String> set1 = new HashSet<>(Arrays.asList(s1.toLowerCase().split("\\s+")));
        Set<String> set2 = new HashSet<>(Arrays.asList(s2.toLowerCase().split("\\s+")));

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        if (union.isEmpty()) return 0.0;
        return (double) intersection.size() / union.size();
    }

    private String generateSuggestions(double score, List<String> missing, double plagiarismScore) {
        StringBuilder sb = new StringBuilder();
        if (score < 50) {
            sb.append("Your score is very low. Try incorporating more keywords from the job description. ");
        } else if (score < 80) {
            sb.append("Good start, but room for improvement. ");
        } else {
            sb.append("Excellent match! ");
        }

        if (!missing.isEmpty()) {
            sb.append("Consider adding these skills if you have them: ").append(String.join(", ", missing.subList(0, Math.min(3, missing.size())))).append(". ");
        }

        if (plagiarismScore > 50) {
            sb.append("Warning: High similarity (").append(String.format("%.2f", plagiarismScore)).append("%) with existing resumes detected. Ensure originality. ");
        }

        return sb.toString();
    }
}
