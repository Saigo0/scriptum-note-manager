package br.com.scriptum.resource;

import br.com.scriptum.model.Document;
import br.com.scriptum.service.DocumentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentResource {

    @Inject
    DocumentService documentService;

    public static class ManualDocumentRequest {
        public String title;
        public List<String> paragraphs;
    }

    public static class ScannedDocumentRequest {
        public String title;
        public String language;
        public String imageBase64;
    }

    @GET
    @Path("/{id}")
    public Response getDocument(@PathParam("id") Long id) {
        Document doc = documentService.findById(id);
        
        if (doc == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Document not found for ID: " + id)
                           .build();
        }
        
        return Response.ok(doc).build();
    }

    @GET
    @Path("/category/{categoryId}")
    public Response getByCategory(@PathParam("categoryId") Long categoryId) {
        List<Document> docs = documentService.findByCategoryId(categoryId);
        return Response.ok(docs).build();
    }

    @Transactional
    public Document createManualDocument(String title, List<String> paragraphs, Long categoryId) {
        validateSize(paragraphs);

        Category category = Category.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }

        Document doc = new Document();
        doc.title = title;
        doc.content = paragraphs;
        doc.status = "MANUALLY_TYPED";
        doc.category = category; 
        doc.persist();
        
        return doc;
    }
    @POST
    @Path("/scanned")
    public Response createScanned(ScannedDocumentRequest request) {
        Document doc = documentService.createScannedDocument(request.title, request.language, request.imageBase64);
        return Response.status(Response.Status.ACCEPTED).entity(doc).build(); 
    }

    @PUT
    @Path("/{id}")
    public Response editDocument(@PathParam("id") Long id, List<String> newParagraphs) {
        try {
            Document doc = documentService.editDocument(id, newParagraphs);
            if (doc == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(doc).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}