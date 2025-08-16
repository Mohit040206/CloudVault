package com.cloudvault.service;

import com.cloudvault.model.Document;
import com.cloudvault.model.User;
import com.cloudvault.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final String uploadDir = "uploads/"; // Local folder to save files

    @Autowired
    private DocumentRepository documentRepository;

    // Upload file
    public Document uploadFile(MultipartFile file, User user) throws IOException {
        // Ensure upload directory exists
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save file to local system
        String filePath = uploadDir + file.getOriginalFilename();
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        // Save metadata to DB
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setFilePath(filePath);
        document.setFileType(file.getContentType()); // capture MIME type
        document.setFileSize(file.getSize());        // capture size in bytes
        document.setUploadedAt(LocalDateTime.now()); // ✅ correct field
        document.setUser(user);

        return documentRepository.save(document);
    }

    // Get document by ID
    public Optional<Document> getDocument(Long id) {
        return documentRepository.findById(id);
    }

    // List documents for a user
    public List<Document> getDocumentsByUser(User user) {
        return documentRepository.findByUser(user); 
    }

    // Delete document
    public boolean deleteDocument(Long id) {
        Optional<Document> optionalDocument = documentRepository.findById(id);
        if (optionalDocument.isPresent()) {
            Document doc = optionalDocument.get();

            // Delete from local storage
            File file = new File(doc.getFilePath());
            if (file.exists()) {
                file.delete();
            }

            // Delete from DB
            documentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
