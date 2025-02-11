package com.filestorage;

import com.accounts.AccountsCollection;
import com.database.MongoUtil;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileStorageService {
    private static FileStorageService instance;
    @Getter
    private Storage storage;

    private FileStorageService() {
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.connect();
        storage = new LocalStorage();
    }

    public static FileStorageService getInstance() {
        if (instance == null) {
            instance = new FileStorageService();
        }
        return instance;
    }

    public String saveFile(File file, String fileExtension) throws IOException {
        FileMetadata metadata = getFileMetadata(file);


        String newFileName = metadata.fileId + fileExtension;
        storage.uploadFile(metadata.fileId + fileExtension, file);

        return newFileName;
    }

    public void deleteFile(String fileId) {
        storage.deleteFile(fileId);
    }

    public File downloadFile(String fileId) throws IOException {
        return storage.downloadFile(fileId);
    }

    private static FileMetadata getFileMetadata(File file) throws IOException {
        return new FileMetadata(
                file.getName(),
                UUID.randomUUID().toString()
        );
    }

}
