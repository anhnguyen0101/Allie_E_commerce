package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin/upload")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class FileUploadController {
    
    private static final String UPLOAD_DIR = "uploads/";
    
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("📤 [FileUpload] ========================================");
        log.info("📤 [FileUpload] POST /api/admin/upload/image");
        log.info("📤 [FileUpload] ========================================");
        log.info("📤 [FileUpload] Filename: {}", file.getOriginalFilename());
        log.info("📤 [FileUpload] Size: {} bytes", file.getSize());
        log.info("📤 [FileUpload] Content-Type: {}", file.getContentType());
        
        try {
            // Create uploads directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("✅ [FileUpload] Created uploads directory: {}", uploadPath.toAbsolutePath());
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;
            
            log.info("📤 [FileUpload] Original filename: {}", originalFilename);
            log.info("📤 [FileUpload] New filename: {}", newFilename);
            
            // Save file
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("✅ [FileUpload] File saved to: {}", filePath.toAbsolutePath());
            
            // Generate URL
            String imageUrl = "http://localhost:8081/uploads/" + newFilename;
            
            log.info("✅ [FileUpload] ========================================");
            log.info("✅ [FileUpload] IMAGE UPLOADED SUCCESSFULLY");
            log.info("✅ [FileUpload] ========================================");
            log.info("✅ [FileUpload] Image URL: {}", imageUrl);
            
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("filename", newFilename);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("❌ [FileUpload] ========================================");
            log.error("❌ [FileUpload] ERROR UPLOADING FILE");
            log.error("❌ [FileUpload] ========================================");
            log.error("❌ [FileUpload] Error message: {}", e.getMessage());
            log.error("❌ [FileUpload] Stack trace:", e);
            
            return ResponseEntity.internalServerError().build();
        }
    }
}
