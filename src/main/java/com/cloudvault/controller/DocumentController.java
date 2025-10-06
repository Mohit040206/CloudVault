package com.cloudvault.controller;

import com.cloudvault.entity.Document;
import com.cloudvault.service.DocumentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    // Upload file
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "description", required = false) String description,
                             HttpSession session,
                             Model model) {
        // Make sure user is logged in
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        try {
            documentService.saveFile(file, description, email); // pass email
            model.addAttribute("success", true);
        } catch (IOException e) {
            model.addAttribute("error", true);
            e.printStackTrace();
        }
        return "upload";
    }




    // Gallery View
    @GetMapping("/gallery")
    public String viewGallery(Model model) {
        List<Document> documents = documentService.getAllFiles();
        model.addAttribute("documents", documents);
        return "gallery";
    }

    // Inline View / Download
    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewFile(@PathVariable Long id, HttpSession session,
                                      @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

        String email = (String) session.getAttribute("email");
        if (email == null) return ResponseEntity.status(401).body("Unauthorized");

        Document doc = documentService.getFileById(id);

        // Check if the logged-in user is the uploader OR shared with this user
        if (!doc.getUser().getEmail().equals(email) && !documentService.isSharedWithUser(doc, email)) {
            return ResponseEntity.status(403).body("You are not allowed to access this file");
        }

        // --- Video / File streaming logic here (seekable if video) ---
        Path path = Path.of(doc.getFilePath());
        String fileType = Files.probeContentType(path);
        long fileSize = Files.size(path);

        if (rangeHeader == null) {
            byte[] fileBytes = Files.readAllBytes(path);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileType))
                    .body(new InputStreamResource(new ByteArrayInputStream(fileBytes)));
        }

        long rangeStart = 0;
        long rangeEnd = fileSize - 1;
        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        rangeStart = Long.parseLong(ranges[0]);
        if (ranges.length > 1 && !ranges[1].isEmpty()) {
            rangeEnd = Long.parseLong(ranges[1]);
        }
        long contentLength = rangeEnd - rangeStart + 1;
        byte[] fileBytes = new byte[(int) contentLength];
        try (var inputStream = Files.newInputStream(path)) {
            inputStream.skip(rangeStart);
            inputStream.read(fileBytes, 0, (int) contentLength);
        }

        return ResponseEntity.status(206)
                .header("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .header("Accept-Ranges", "bytes")
                .contentLength(contentLength)
                .contentType(MediaType.parseMediaType(fileType))
                .body(new InputStreamResource(new ByteArrayInputStream(fileBytes)));
    }

}
