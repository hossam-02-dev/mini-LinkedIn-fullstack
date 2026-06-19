package com.example.miniLinkedin.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadRestController {
	 @Value("${app.upload.dir:uploads/}")
	    private String uploadDir;

	    @PostMapping("/image")
	    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
	        // Créer le dossier s'il n'existe pas
	        Path uploadPath = Paths.get(uploadDir);
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }

	        // Générer un nom unique
	        String originalFilename = image.getOriginalFilename();
	        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
	        String fileName = UUID.randomUUID().toString() + extension;

	        // Sauvegarder le fichier
	        Path filePath = uploadPath.resolve(fileName);
	        Files.copy(image.getInputStream(), filePath);

	        // Retourner l'URL relative
	        Map<String, String> response = new HashMap<>();
	        response.put("imageUrl", "/uploads/" + fileName);
	        return ResponseEntity.ok(response);
	    }
}
