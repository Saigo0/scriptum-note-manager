package br.com.scriptum.resource;

import br.com.scriptum.model.Category;
import br.com.scriptum.service.CategoryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    CategoryService categoryService;

    public static class CategoryRequest {
        public String title;
    }

    @POST
    public Response create(CategoryRequest request) {
        try {
            Category category = categoryService.createCategory(request.title);
            return Response.status(Response.Status.CREATED).entity(category).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    public Response list() {
        return Response.ok(categoryService.listCategories()).build();
    }
}