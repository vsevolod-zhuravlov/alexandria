package com.endpoints.resources;

import com.filestorage.FileStorageService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.io.File;
import java.io.IOException;

@Path("/download")
public class DownloadResource {
    private static final FileStorageService fileStorageService = FileStorageService.getInstance();

    @GET
    public File download(@QueryParam("fileId") String fileId) throws IOException {
        return fileStorageService.downloadFile(fileId);
    }
}
