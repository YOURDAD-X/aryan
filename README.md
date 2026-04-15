# Full Stack Resume ATS Scorer and Builder 🚀

A comprehensive, modular Spring Boot application designed for academic submission. This project covers all core aspects of an AI-driven, multithreaded ATS System.

## 🛠️ Tech Stack & Features
- **Backend**: Java 17+, Spring Boot
- **Frontend**: HTML5, CSS3 (Glassmorphism UI), Vanilla JS
- **Database**: MySQL 
- **Parsing Engine**: Apache Tika (PDF/DOCX Extraction)
- **Concurrency**: `CompletableFuture` for heavy Multithreaded ATS scoring.
- **Security**: Base abstraction of authentication, custom User entities.
- **Verification mechanism**: Custom pseudo-blockchain hashing (SHA-256 ledger).
- **Core OOP Concepts**: Encapsulation (Entities), Abstraction (Service Interfaces), inheritance/polymorphism patterns implemented.

## 📁 Academic Output Requirements Fulfilled
1. **Multithreading**: ATS Engine (`AtsScoringService.java`) runs keyword extraction and plagiarism checks concurrently.
2. **File Handling**: PDF and DOCX parsing powered by Apache Tika.
3. **Plagiarism Checking**: Compares uploaded document against internal resume DB using text similarity mathematics.
4. **Blockchain**: Hash chains generated and stored in the `blockchain_ledger` table.
5. **Credit System**: Starts with 10 credits upon registration.

## 🚀 How to Run

### 1. Database Setup
1. Ensure your local MySQL server is running on `localhost:3306`.
2. Ensure you have a user `root` with a blank password `""`. If you have a different password, update `src/main/resources/application.properties`.
3. The database `resume_ats_db` and its tables will be created **automatically** on boot via Hibernate (`ddl-auto=update`).
4. (Optional) A `schema.sql` is provided in the root directory if manual intervention is required.

### 2. Application boot
To run without installing Maven explicitly (if your IDE supports it, run `AtsApplication.java`), or if you have Maven installed:
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Accessing the Application
Once the Spring Boot application is running (Tomcat initialized on Port 8080):
Open `src/main/resources/static/index.html` directly in your Web Browser. (Alternatively, if Spring boot serves static files, go to `http://localhost:8080/index.html`).

*Enjoy your fully functional ATS Platform!*
