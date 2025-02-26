package com.endpoints.filestorage;

import com.sun.jersey.multipart.FormDataParam;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


@Path("/taskfile")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class FileResource {
    private static final FileStorageService fileStorageService = FileStorageService.getInstance();
    private static final FileMetadataCollection fileMetadataCollection = fileStorageService.getMetadataCollection();


    @GET
    public File download(@QueryParam("projectAddress") String projectAddress,
                            @QueryParam("taskId") String taskId,
                            @QueryParam("studentAddress") String studentAddress) throws IOException {
        FileMetadata metadata = fileMetadataCollection.get(projectAddress, taskId, studentAddress);

        File file = fileStorageService.downloadFile(metadata.fileId);

        File renamedFile = new File(file.getParent(), file.getName() + metadata.fileExtension);

        // Rename the file
        if (file.renameTo(renamedFile)) {
            return renamedFile;
        } else {
            throw new IOException("Failed to rename the file.");
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@FormDataParam("file") File file,
                           @QueryParam("projectAddress") String projectAddress,
                           @QueryParam("taskId") String taskId,
                           @QueryParam("studentAddress") String studentAddress,
                           @QueryParam("fileExtension") String fileExtension) {
        try {
            if (file == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("No file uploaded").build();
            }
            FileMetadata metadata = new FileMetadata(file.getName(), projectAddress, taskId, studentAddress, fileExtension);

            fileStorageService.saveFile(file, metadata);

            return Response.ok().build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
