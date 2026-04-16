package com.ats.service;

import com.ats.model.Resume;
import com.ats.model.User;
import com.ats.repository.ResumeRepository;
import com.ats.repository.UserRepository;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlockchainService blockchainService;

    @Transactional
    public Resume parseAndSaveResume(Long userId, MultipartFile file) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Deduct credit
        if (user.getCredits() <= 0) {
            throw new RuntimeException("Insufficient credits to parse resume");
        }
        user.setCredits(user.getCredits() - 1);
        userRepository.save(user);

        // Parse with Tika
        Tika tika = new Tika();
        String extractedText;
        try (InputStream stream = file.getInputStream()) {
            extractedText = tika.parseToString(stream);
        }

        Resume resume = new Resume();
        resume.setUser(user);
        resume.setFilename(file.getOriginalFilename());
        resume.setExtractedText(extractedText);
        
        Resume savedResume = resumeRepository.save(resume);

        // Add to blockchain ledger
        blockchainService.addBlock(savedResume, extractedText);

        return savedResume;
    }
}
