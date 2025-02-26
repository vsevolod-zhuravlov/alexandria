package com.endpoints.filestorage;

import com.database.MongoUtil;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

public class FileStorageService {
    private static FileStorageService instance;
    private FileMetadataCollection fileMetadataCollection;
    @Getter
    private Storage storage;

    private FileStorageService() {
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.connect();
        fileMetadataCollection = new FileMetadataCollection(mongoUtil);
        storage = new LocalStorage();
    }

    public static FileStorageService getInstance() {
        if (instance == null) {
            instance = new FileStorageService();
        }
        return instance;
    }

    public void saveFile(File file, FileMetadata metadata) throws IOException {
        fileMetadataCollection.save(metadata);
        storage.uploadFile(file);
    }

    public void deleteFile(String fileId) {
        storage.deleteFile(fileId);
    }

    public File downloadFile(String fileId) throws IOException {
        return storage.downloadFile(fileId);
    }



    public FileMetadataCollection getMetadataCollection() {
        return fileMetadataCollection;
    }
}
