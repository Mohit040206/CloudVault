package com.cloudvault.repository;

import com.cloudvault.model.Document;
import com.cloudvault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUser(User user);
    List<Document> findByUserAndFileNameContainingIgnoreCase(User user, String fileName);


}
