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
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    private final String UPLOAD_DIR = "uploads/";

    public void saveFile(MultipartFile file, String description, String email) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Empty file not allowed");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = file.getOriginalFilename();
        String fileType = file.getContentType();

        // Generate unique stored filename
        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        Path storedFilePath = Paths.get(UPLOAD_DIR, storedFileName);

        // Save file as-is (no HEIC conversion)
        Files.copy(file.getInputStream(), storedFilePath, StandardCopyOption.REPLACE_EXISTING);

        // Fetch user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Save document record
        Document document = new Document();
        document.setFileName(storedFileName);
        document.setFilePath(storedFilePath.toString());
        document.setFileType(fileType);
        document.setFileSize(file.getSize());
        document.setUploadedAt(LocalDateTime.now());
        document.setExpiryAt(null);
        document.setUser(user);
       

        documentRepository.save(document);
    }

    public List<Document> getFilesByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return documentRepository.findByUser(user);
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

    public boolean deleteFile(Long id, String email) throws IOException {
        Document document = getFileById(id);

        if (!document.getUser().getEmail().equals(email)) {
            return false; // Prevent deleting others' files
        }

        Path path = Paths.get(document.getFilePath());
        Files.deleteIfExists(path);
        documentRepository.delete(document);
        return true;
    }

    public List<Document> searchDocumentsByUser(String keyword, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return documentRepository.findByUserAndFileNameContainingIgnoreCase(user, keyword);
    }
}
