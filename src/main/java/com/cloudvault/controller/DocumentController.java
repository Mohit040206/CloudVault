package com.cloudvault.controller;

import com.cloudvault.entity.Document;
import com.cloudvault.service.DocumentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    //  Upload file (fixed success redirect)
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "description", required = false) String description,
                             HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/login";

        try {
            documentService.saveFile(file, description, email);
            return "redirect:/documents/gallery?uploaded=true";  // ✅ success
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/documents/gallery?error=true";      // ❌ failure
        }
    }

    //  Gallery
    @GetMapping("/gallery")
    public String viewGallery(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/login";

        List<Document> documents = documentService.getFilesByUser(email);
        model.addAttribute("documents", documents);
        return "gallery";
    }

    //View file
    @GetMapping("/view/{id}")
    public ResponseEntity<ResourceRegion> viewFile(@PathVariable Long id,
                                                   @RequestHeader(value = "Range", required = false) String rangeHeader,
                                                   HttpSession session) throws IOException {

        String email = (String) session.getAttribute("email");
        if (email == null) return ResponseEntity.status(401).build();

        Document doc = documentService.getFileById(id);
        if (!doc.getUser().getEmail().equals(email)) {
            return ResponseEntity.status(403).build();
        }

        Path path = Path.of(doc.getFilePath());
        Resource video = new UrlResource(path.toUri());
        long contentLength = video.contentLength();
        ResourceRegion region = new ResourceRegion(video, 0, contentLength);

        if (rangeHeader != null) {
            List<HttpRange> httpRanges = HttpRange.parseRanges(rangeHeader);
            HttpRange httpRange = httpRanges.get(0);
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            region = new ResourceRegion(video, start, end - start + 1);
        }

        String fileType = Files.probeContentType(path);
        return ResponseEntity.status(rangeHeader != null ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK)
                .contentType(MediaType.parseMediaType(fileType))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(region);
    }

    //  Download file
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, HttpSession session) throws IOException {
        String email = (String) session.getAttribute("email");
        if (email == null) return ResponseEntity.status(401).build();

        Document doc = documentService.getFileById(id);
        if (!doc.getUser().getEmail().equals(email)) {
            return ResponseEntity.status(403).build();
        }

        Path path = Path.of(doc.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }

    //  Delete file (fixed redirect)
    @PostMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/login";

        try {
            boolean deleted = documentService.deleteFile(id, email);
            if (deleted)
                return "redirect:/documents/gallery?deleted=true";
            else
                return "redirect:/documents/gallery?error=true";
        } catch (Exception e) {
            return "redirect:/documents/gallery?error=true";
        }
    }
}
