package com.endpoints;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

@ApplicationPath("/api")
public class JettyServerResourceConfig extends ResourceConfig {

    public JettyServerResourceConfig() {
        packages("com.endpoints");
        register(RolesAllowedDynamicFeature.class);
        //register(AuthenticationFilter.class);
    }
}
