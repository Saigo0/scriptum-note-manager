package br.com.scriptum;

import br.com.scriptum.model.Document;
import br.com.scriptum.resource.CategoryResource;
import br.com.scriptum.service.CategoryService;
import br.com.scriptum.model.Category;
import jakarta.transaction.Transactional;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import br.com.scriptum.configuracao.IntegrationTest;
import java.util.List;
import io.quarkus.narayana.jta.QuarkusTransaction;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Map;
import java.util.HashMap;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@IntegrationTest
public class DocumentIntegrationTest {

    @Inject
    @Any
    InMemoryConnector connector;

    @Inject CategoryResource categoryResource;
    @Inject CategoryService categoryService;

    @BeforeEach
    public void setup() {
        connector.sink("ocr-processing").clear();
    }

    @Test
    @Transactional
    public void testCreateManualDocumentSuccess() {
        Category category = new Category();
        category.title = "Categoria de Teste";
        category.persist();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("categoryId", category.id); 
        requestBody.put("title", "Meu Novo Documento");
        requestBody.put("paragraphs", List.of("Primeiro parágrafo do teste."));

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/documents/manual")
        .then()
            .statusCode(201)
            .body("title", equalTo("Meu Novo Documento"))
            .body("category.id", equalTo(category.id.intValue())); 
    }

    @Test
    public void testRejectDocumentLargerThan2MB() {
        String hugeText = "*".repeat(2_100_000); 
        
        JsonObject request = new JsonObject()
                .put("title", "Heavy Document")
                .put("paragraphs", List.of(hugeText));

        given()
            .contentType(ContentType.JSON)
            .body(request.encode())
        .when()
            .post("/documents/manual")
        .then()
            .statusCode(400); 
    }

    @Test
    public void testCreateScannedDocumentSendsMessageToQueue() {
        JsonObject request = new JsonObject()
                .put("title", "Whiteboard Photo")
                .put("language", "en")
                .put("imageBase64", "fake-base64");

        given()
            .contentType(ContentType.JSON)
            .body(request.encode())
        .when()
            .post("/documents/scanned")
        .then()
            .statusCode(202) 
            .body("status", is("SCANNING"));

        InMemorySink<JsonObject> sink = connector.sink("ocr-processing");
        assertEquals(1, sink.received().size());
    }

    @Test
    public void testEditDocumentSuccess() {
        Document doc = new Document();
        doc.title = "Original Document";
        doc.status = "MANUALLY_TYPED";
        QuarkusTransaction.requiringNew().run(() -> doc.persist());

        List<String> newParagraphs = List.of("Updated text on edit.");

        given()
            .contentType(ContentType.JSON)
            .body(newParagraphs)
        .when()
            .put("/documents/" + doc.id)
        .then()
            .statusCode(200) 
            .body("content[0]", is("Updated text on edit."))
            .body("status", is("MANUALLY_TYPED"));
    }

    @Test
    public void testEditNonExistentDocumentReturns404() {
        List<String> newParagraphs = List.of("Doesn't matter");

        given()
            .contentType(ContentType.JSON)
            .body(newParagraphs)
        .when()
            .put("/documents/9999999") 
        .then()
            .statusCode(404); 
    }

    @Test
    public void testDirectServiceCoverage() {
        CategoryResource.CategoryRequest reqCategory = new CategoryResource.CategoryRequest();
        reqCategory.title = "Direct Coverage Category";
        
        categoryResource.create(reqCategory);
        assertNotNull(categoryResource.list());
        assertNotNull(categoryService.listCategories());
    }
}