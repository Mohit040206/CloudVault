package com.cloudvault.controller;

import com.cloudvault.entity.Document;
import com.cloudvault.service.DocumentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
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

    // ✅ Upload file
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "description", required = false) String description,
                             HttpSession session,
                             Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/login";

        try {
            documentService.saveFile(file, description, email);
            model.addAttribute("success", true);
        } catch (IOException e) {
            model.addAttribute("error", true);
            e.printStackTrace();
        }
        return "redirect:/documents/gallery?error=true";
    }

    // ✅ Gallery (shows only logged-in user’s files)
    @GetMapping("/gallery")
    public String viewGallery(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/login";

        List<Document> documents = documentService.getFilesByUser(email);
        model.addAttribute("documents", documents);
        return "gallery";
    }

    // ✅ View / Stream file

    @GetMapping("/view/{id}")
    public ResponseEntity<ResourceRegion> viewFile(@PathVariable Long id,
                                                   @RequestHeader(value = "Range", required = false) String rangeHeader,
                                                   HttpSession session) throws IOException {

        String email = (String) session.getAttribute("email");
        if (email == null) return ResponseEntity.status(401).build();

        Document doc = documentService.getFileById(id);

        // Only uploader can access
        if (!doc.getUser().getEmail().equals(email)) {
            return ResponseEntity.status(403).build();
        }

        Path path = Path.of(doc.getFilePath());
        Resource video = new UrlResource(path.toUri());
        long contentLength = video.contentLength();

        // Default: full region
        ResourceRegion region = new ResourceRegion(video, 0, contentLength);

        // If Range header exists, parse it
        if (rangeHeader != null) {
            List<HttpRange> httpRanges = HttpRange.parseRanges(rangeHeader);
            HttpRange httpRange = httpRanges.get(0);
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = end - start + 1;
            region = new ResourceRegion(video, start, rangeLength);
        }

        String fileType = Files.probeContentType(path);

        return ResponseEntity.status(rangeHeader != null ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK)
                .contentType(MediaType.parseMediaType(fileType))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(region);
    }

    // ✅ Delete file
    @PostMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id, HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/login";

        try {
            boolean deleted = documentService.deleteFile(id, email);
            if (deleted) {
                model.addAttribute("successMessage", "File deleted successfully!");
            } else {
                model.addAttribute("errorMessage", "You cannot delete this file!");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error deleting file: " + e.getMessage());
        }

        List<Document> documents = documentService.getFilesByUser(email);
        model.addAttribute("documents", documents);
        return "redirect:/documents/gallery?error=true";
    }
}
