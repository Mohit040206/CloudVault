package com.cloudvault.service;

import com.cloudvault.entity.Document;

import com.cloudvault.model.User;
import com.cloudvault.repository.DocumentRepository;
import com.cloudvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository; // NEW

    private final String UPLOAD_DIR = "uploads/";

    public void saveFile(MultipartFile file, String description, String email) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Empty file not allowed");
        }

        // Create upload directory if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        String filePath = UPLOAD_DIR + System.currentTimeMillis() + "_" + fileName;

        // Save file to disk
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        // Fetch the user from email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Document document = new Document();
        document.setFileName(fileName);
        document.setFilePath(filePath);
        document.setFileType(Files.probeContentType(Paths.get(filePath)));
        document.setFileSize(file.getSize());
        document.setUploadedAt(LocalDateTime.now());
        document.setExpiryAt(null);
        document.setUser(user); // set the logged-in user
        documentRepository.save(document);
    }

    public List<Document> getAllFiles() {
        return documentRepository.findAll();
    }

    public Document getFileById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + id));
    }

    public byte[] downloadFile(Long id) throws IOException {
        Document document = getFileById(id);
        Path path = Paths.get(document.getFilePath());
        return Files.readAllBytes(path);
    }
    public boolean isSharedWithUser(Document doc, String email) {
        // Check in your sharing table or password mapping
        // Return true if this user has access
        return doc.getSharedUsers().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

}
