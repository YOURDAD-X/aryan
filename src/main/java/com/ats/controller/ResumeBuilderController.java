package com.ats.controller;

import com.ats.dto.ResumeBuilderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/builder")
@CrossOrigin(origins = "*")
public class ResumeBuilderController {

    @PostMapping("/generate")
    public ResponseEntity<String> generateResumeHtml(@RequestBody ResumeBuilderRequest request) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
            .append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 40px; color: #333; max-width: 800px; margin: auto; }")
            .append("h1 { border-bottom: 2px solid #2b6cb0; padding-bottom: 10px; color: #2b6cb0; }")
            .append("h2 { color: #2d3748; margin-top: 30px; border-bottom: 1px solid #e2e8f0; }")
            .append(".contact { text-align: center; margin-bottom: 30px; font-size: 1.1em; color: #4a5568; }")
            .append(".section { margin-bottom: 20px; }")
            .append(".item-title { font-weight: bold; font-size: 1.1em; }")
            .append(".item-subtitle { font-style: italic; color: #718096; margin-bottom: 5px; }")
            .append("</style></head><body>");

        // Header
        html.append("<h1 style='text-align:center; margin-bottom: 5px;'>").append(request.getFullName()).append("</h1>");
        html.append("<div class='contact'>").append(request.getEmail()).append(" | ").append(request.getPhone()).append("</div>");

        // Summary
        if (request.getSummary() != null && !request.getSummary().isEmpty()) {
            html.append("<h2>Professional Summary</h2>");
            html.append("<p>").append(request.getSummary()).append("</p>");
        }

        // Skills
        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            html.append("<h2>Skills</h2>");
            html.append("<p>").append(String.join(", ", request.getSkills())).append("</p>");
        }

        // Experience
        if (request.getExperiences() != null && !request.getExperiences().isEmpty()) {
            html.append("<h2>Experience</h2>");
            for (ResumeBuilderRequest.Experience exp : request.getExperiences()) {
                html.append("<div class='section'>");
                html.append("<div class='item-title'>").append(exp.getRole()).append(" at ").append(exp.getCompany()).append("</div>");
                html.append("<div class='item-subtitle'>").append(exp.getDuration()).append("</div>");
                html.append("<p>").append(exp.getDescription() != null ? exp.getDescription() : "").append("</p>");
                html.append("</div>");
            }
        }

        // Education
        if (request.getEducations() != null && !request.getEducations().isEmpty()) {
            html.append("<h2>Education</h2>");
            for (ResumeBuilderRequest.Education edu : request.getEducations()) {
                html.append("<div class='section'>");
                html.append("<div class='item-title'>").append(edu.getDegree()).append("</div>");
                html.append("<div class='item-subtitle'>").append(edu.getInstitution()).append(" (").append(edu.getYear()).append(")</div>");
                html.append("</div>");
            }
        }

        html.append("</body></html>");

        return ResponseEntity.ok(html.toString());
    }
}
