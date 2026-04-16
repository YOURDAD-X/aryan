package com.ats.dto;

import lombok.Data;
import java.util.List;

@Data
public class ResumeBuilderRequest {
    private String fullName;
    private String email;
    private String phone;
    private String summary;
    private List<Experience> experiences;
    private List<String> skills;
    private List<Education> educations;
    
    @Data
    public static class Experience {
        private String company;
        private String role;
        private String duration;
        private String description;
    }
    
    @Data
    public static class Education {
        private String institution;
        private String degree;
        private String year;
    }
}
