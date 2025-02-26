package com.endpoints.filestorage;

import java.io.File;

public interface Storage {
    public void uploadFile(File file);

    void uploadFile(String fileName, File file);

    public File downloadFile(String name);

    public void deleteFile(String name);
}
