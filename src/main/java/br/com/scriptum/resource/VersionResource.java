package br.com.scriptum.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/version")
public class VersionResource {

    @ConfigProperty(name = "quarkus.application.version")
    String appVersion;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getVersion() {
        return appVersion;
    }
}