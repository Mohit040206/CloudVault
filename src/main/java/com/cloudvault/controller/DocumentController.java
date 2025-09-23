package com.cloudvault.controller;

import com.cloudvault.model.Document;
import com.cloudvault.model.User;
import com.cloudvault.repository.UserRepository;
import com.cloudvault.service.DocumentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserRepository userRepository;


    // ========== Upload ==========

    @PostMapping("/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file,
                                 HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login.html";
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "redirect:/login.html";
        }

        documentService.uploadFile(file, userOpt.get());
        return "redirect:/upload" + "?success=true"; // after success, reload same page with message
    }


    // ========== View All Documents of Logged-in User ==========
    @GetMapping("/mydocs")
    public String listUserDocuments(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login.html";
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "redirect:/login.html";
        }

        List<Document> docs = documentService.getDocumentsByUser(userOpt.get());
        model.addAttribute("documents", docs);
        return "mydocs.html"; // return mydocs.html with list
    }

    // ========== Download ==========
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id, HttpSession session) {


        String email = (String) session.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        Resource file = documentService.downloadDocument(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    // ========== Delete ==========
    @GetMapping("/delete/{id}")
    public String deleteDocument(@PathVariable Long id, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login.html";
        }

        documentService.deleteDocument(id);
        return "redirect:/document/mydocs";
    }
    // ========== Search Documents ==========
    @GetMapping("/search")
    public String searchDocuments(@RequestParam("keyword") String keyword,
                                  HttpSession session,
                                  Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login.html";
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "redirect:/login.html";
        }

        // Only search inside logged-in user’s documents
        List<Document> docs = documentService.searchDocumentsByUser(keyword, userOpt.get());
        model.addAttribute("documents", docs);
        model.addAttribute("keyword", keyword);

        return "mydocs"; // reuse same page to display search results
    }

}
