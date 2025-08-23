package com.cloudvault.service;

import com.cloudvault.model.Document;
import com.cloudvault.model.User;
import com.cloudvault.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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

    private final String uploadDir = "uploads/"; // Local folder

    @Autowired
    private DocumentRepository documentRepository;

    // Upload file
    public Document uploadFile(MultipartFile file, User user) {
        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = uploadDir + file.getOriginalFilename();
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());

            Document document = new Document();
            document.setFileName(file.getOriginalFilename());
            document.setFilePath(filePath);
            document.setFileType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setUploadedAt(LocalDateTime.now());
            document.setUser(user);

            return documentRepository.save(document);

        } catch (IOException e) {
            throw new RuntimeException("Error while uploading file: " + e.getMessage(), e);
        }
    }

    // Get all documents for a user
    public List<Document> getDocumentsByUser(User user) {
        return documentRepository.findByUser(user);
    }

    // Get single document
    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    // Download document as Resource
    public Resource downloadDocument(Long id) {
        Optional<Document> optionalDoc = documentRepository.findById(id);
        if (optionalDoc.isPresent()) {
            File file = new File(optionalDoc.get().getFilePath());
            if (file.exists()) {
                return new FileSystemResource(file);
            }
        }
        return null;
    }

    // Delete document
    public boolean deleteDocument(Long id) {
        Optional<Document> optionalDoc = documentRepository.findById(id);
        if (optionalDoc.isPresent()) {
            Document doc = optionalDoc.get();
            File file = new File(doc.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            documentRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public List<Document> searchDocumentsByUser(String fileName, User user) {
        return documentRepository.findByUserAndFileNameContainingIgnoreCase(user,fileName);
    }

}
