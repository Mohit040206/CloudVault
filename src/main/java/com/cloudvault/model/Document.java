package com.cloudvault.model;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Original file name (what the user uploaded)
    @Column(nullable = false)
    private String fileName;

    // Path where file is stored locally (or cloud storage path)
    @Column(nullable = false, unique = true)
    private String filePath;

    // MIME type or extension (pdf, jpg, etc.)
    private String fileType;

    // Size of file in bytes
    private Long fileSize;

    // Timestamp when file was uploaded
    private LocalDateTime uploadedAt;

    // Expiry time (optional - if you want auto-delete after time)
    private LocalDateTime expiryAt;

    // Each file belongs to a user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ----- Constructors -----
    public Document() {}

    public Document(String fileName, String filePath, String fileType, Long fileSize,
                    LocalDateTime uploadedAt, LocalDateTime expiryAt, User user) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.expiryAt = expiryAt;
        this.user = user;
    }

    // ----- Getters & Setters -----
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public LocalDateTime getExpiryAt() {
        return expiryAt;
    }

    public void setExpiryAt(LocalDateTime expiryAt) {
        this.expiryAt = expiryAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // ----- toString -----
    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", uploadedAt=" + uploadedAt +
                ", expiryAt=" + expiryAt +
                ", user=" + (user != null ? user.getId() : null) +
                '}';
    }
}
