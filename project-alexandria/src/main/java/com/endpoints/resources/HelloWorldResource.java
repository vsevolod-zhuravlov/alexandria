package com.endpoints.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Deprecated
@Path("/hello-world")
public class HelloWorldResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMessageScenario() {
        return "Hello World";
    }
}
