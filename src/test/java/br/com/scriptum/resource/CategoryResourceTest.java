package br.com.scriptum.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
public class CategoryResourceTest {

    @Test
    public void testCreateCategorySuccess() {
        // Refletindo os nomes do inglês ("title" em vez de "nome")
        JsonObject request = new JsonObject().put("title", "College Subjects");

        given()
            .contentType(ContentType.JSON)
            .body(request.encode())
        .when()
            // A nova rota
            .post("/categories")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", is("College Subjects"));
    }

    @Test
    public void testRejectCategoryWithoutTitle() {
        JsonObject request = new JsonObject().put("title", "");

        given()
            .contentType(ContentType.JSON)
            .body(request.encode())
        .when()
            .post("/categories")
        .then()
            .statusCode(400);
    }

    @Test
    public void testListCategories() {
        given()
        .when()
            .get("/categories")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(0));
    }
}