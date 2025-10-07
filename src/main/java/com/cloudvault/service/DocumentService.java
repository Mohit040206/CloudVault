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
    private UserRepository userRepository;

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
        document.setUser(user);

        documentRepository.save(document);
    }

    // ✅ Fetch only the logged-in user's files
    public List<Document> getFilesByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return documentRepository.findByUser(user);
    }

    // ✅ Fetch file by ID
    public Document getFileById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + id));
    }

    // ✅ Download file (for viewing)
    public byte[] downloadFile(Long id) throws IOException {
        Document document = getFileById(id);
        Path path = Paths.get(document.getFilePath());
        return Files.readAllBytes(path);
    }

    // ✅ Delete file (only if it belongs to the logged-in user)
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
}
