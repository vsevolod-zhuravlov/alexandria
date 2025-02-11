package com.filestorage;

import java.io.*;
import java.nio.file.*;

public class LocalStorage implements Storage {
    private static final String STORAGE_DIR = "uploads/";

    public LocalStorage() {
        File directory = new File(STORAGE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @Override
    public void uploadFile(String fileName, File file) {
        try {
            Path filePath = Paths.get(STORAGE_DIR, fileName);
            Files.copy(file.toPath(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error saving file: " + fileName, e);
        }
    }

    @Override
    public File downloadFile(String fileName) {
        Path filePath = Paths.get(STORAGE_DIR, fileName);
        return filePath.toFile();
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(STORAGE_DIR, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file: " + fileName, e);
        }
    }
}
