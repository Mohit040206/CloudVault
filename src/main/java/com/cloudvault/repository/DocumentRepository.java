package com.cloudvault.repository;

import com.cloudvault.model.Document;
import com.cloudvault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUser(User user);
    List<Document> findByUserAndFileNameContainingIgnoreCase(User user, String fileName);


}
