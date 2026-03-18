package com.bonheur.portfolio.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class FileUploadUtil {

    public static final Set<String> DEFAULT_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    public static final String BASE_UPLOAD_DIR = "uploads";

    public String uploadFile(MultipartFile file, String subfolder, String namePrefix) {
        return uploadFile(file, subfolder, DEFAULT_IMAGE_EXTENSIONS, namePrefix);
    }

    public String uploadFile(MultipartFile file, String subfolder, Set<String> allowedExtensions, String namePrefix) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String normalizedSubfolder = normalizeSubfolder(subfolder);
        Path folderPath = Paths.get(BASE_UPLOAD_DIR).resolve(normalizedSubfolder);

        try {
            if (Files.notExists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                throw new IllegalArgumentException("Original filename is required");
            }

            String extension = extractExtension(originalFilename).orElse("");
            if (!extension.isBlank() && !allowedExtensions.contains(extension.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("File type not allowed: " + extension);
            }

            String prefix = sanitizeName(namePrefix);
            String randomId = UUID.randomUUID().toString();
            String finalName = (prefix.isBlank() ? "file" : prefix) + "_" + randomId;
            if (!extension.isBlank()) {
                finalName += "." + extension;
            }

            Path destination = folderPath.resolve(finalName);
            Files.write(destination, file.getBytes());

            return normalizedSubfolder + "/" + finalName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public String getFileUrl(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            return null;
        }

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/uploads/")
                .path(storedPath)
                .toUriString();
    }

    private String normalizeSubfolder(String subfolder) {
        if (subfolder == null || subfolder.isBlank()) {
            return "";
        }
        String sanitized = subfolder.trim().replaceAll("[\\\\/]+", "").replaceAll("[^a-zA-Z0-9_-]", "_");
        return sanitized;
    }

    private String sanitizeName(String name) {
        if (name == null) {
            return "";
        }
        String cleaned = name.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
        return cleaned.isBlank() ? "" : cleaned;
    }

    private java.util.Optional<String> extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return java.util.Optional.empty();
        }
        String ext = filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT).trim();
        return ext.isBlank() ? java.util.Optional.empty() : java.util.Optional.of(ext);
    }

}
