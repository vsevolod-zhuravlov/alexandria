package com.filestorage;

import java.io.File;

public interface Storage {
    public void uploadFile(String name, File file);

    public File downloadFile(String name);

    public void deleteFile(String name);
}
