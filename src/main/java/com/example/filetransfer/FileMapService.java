package com.example.filetransfer;

import org.springframework.stereotype.Service;

import com.example.filetransfer.service.FileInfo;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileMapService {

    // In-memory map. For production, use a database (e.g., Redis or SQL)
    private final Map<String, FileInfo> fileMap = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String generateCodeAndMap(String originalFilename, String uniqueFilename) {
        String code;
        do {
            // Generate a 6-digit code (100000 - 999999)
            code = String.valueOf(100000 + random.nextInt(900000));
        } while (fileMap.containsKey(code)); // Ensure code is unique

        fileMap.put(code, new FileInfo(originalFilename, uniqueFilename));
        return code;
    }

    public FileInfo getFileInfo(String code) {
        return fileMap.get(code);
    }
}