package com.bhoomidarpan.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try {

            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "resource_type", "raw",
                            "folder", "bhoomidarpan/property-documents",
                            "use_filename", true,
                            "unique_filename", true,
                            "type", "upload",
                            "access_mode", "public",
                            "format", "pdf"   // 🔥 ADD THIS
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            throw new RuntimeException("Cloud upload failed: " + e.getMessage());
        }
    }
}