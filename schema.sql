-- Database Initialization Settings
CREATE DATABASE IF NOT EXISTS resume_ats_db;
USE resume_ats_db;

-- Note: Spring Boot (Hibernate) is configured with spring.jpa.hibernate.ddl-auto=update
-- This means it will automatically create the required tables based on our entities upon first boot.
-- However, if you would like to manually create them, here is the schema:

CREATE TABLE `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `credits` INT NOT NULL DEFAULT 10
);

CREATE TABLE `resumes` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `filename` VARCHAR(255),
  `extracted_text` TEXT,
  `upload_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
);

CREATE TABLE `ats_scores` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `resume_id` BIGINT NOT NULL,
  `job_description` TEXT,
  `score_percentage` DOUBLE,
  `plagiarism_score` DOUBLE,
  `matched_keywords` TEXT,
  `missing_keywords` TEXT,
  `suggestions` TEXT,
  `calculation_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`resume_id`) REFERENCES `resumes`(`id`) ON DELETE CASCADE
);

CREATE TABLE `blockchain_ledger` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `resume_id` BIGINT NOT NULL,
  `file_hash` VARCHAR(255) NOT NULL UNIQUE,
  `previous_hash` VARCHAR(255) NOT NULL,
  `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`resume_id`) REFERENCES `resumes`(`id`) ON DELETE CASCADE
);
