package com.endpoints.resources;

import com.filestorage.FileStorageService;
import com.sun.jersey.multipart.FormDataParam;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;



@Path("/upload")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class UploadResource {
    private static final FileStorageService fileStorageService = FileStorageService.getInstance();

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@FormDataParam("file") File file,
                           @QueryParam("extension") String extension) {
        try {
            if (file == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("No file uploaded").build();
            }
            System.out.println(extension);
            String fileId = fileStorageService.saveFile(file, trimFileExtension(extension));

            return Response.ok().entity("{\"fileId\":\"" + fileId + "\"}").build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private String trimFileExtension(String extension) {
        if(extension.charAt(0) != '.') {
            extension = "." + extension;
        }
        return extension;
    }
}
