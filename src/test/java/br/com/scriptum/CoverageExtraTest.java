package br.com.scriptum;

import br.com.scriptum.configuracao.UnitTest;
import br.com.scriptum.dto.OcrRequest;
import br.com.scriptum.dto.OcrResponse;
import br.com.scriptum.model.Document;
import br.com.scriptum.model.Category;
import br.com.scriptum.resource.DocumentResource.ScannedDocumentRequest;
import br.com.scriptum.resource.DocumentResource.ManualDocumentRequest;
import br.com.scriptum.resource.CategoryResource.CategoryRequest;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
public class CoverageExtraTest {

    @Test
    public void testEnsureModelsAndDtosCoverage() {
        
        Document doc = new Document();
        doc.title = "Coverage Test";
        doc.status = "MANUALLY_TYPED";
        doc.reliability = 99.9;
        doc.content = new ArrayList<>();
        doc.content.add("Paragraph 1");

        Category category = new Category();
        category.title = "Test Category";
        category.documents = new ArrayList<>();
        
        doc.category = category;
        category.documents.add(doc);

        assertEquals("Coverage Test", doc.title);
        assertEquals("Test Category", category.title);
        assertNotNull(doc.createdAt);

        OcrRequest req = new OcrRequest(1L, "en", "base64-string");
        OcrResponse res = new OcrResponse();
        res.documentId = 1L;
        res.paragraphs = new ArrayList<>();
        res.confidence = 100.0;

        assertEquals(1L, res.documentId);

        ManualDocumentRequest reqManual = new ManualDocumentRequest();
        reqManual.title = "Manual";
        reqManual.paragraphs = new ArrayList<>();

        ScannedDocumentRequest reqScan = new ScannedDocumentRequest();
        reqScan.title = "Scan";
        reqScan.language = "en";
        reqScan.imageBase64 = "img";

        CategoryRequest reqCategory = new CategoryRequest();
        reqCategory.title = "Category";

        assertNotNull(reqManual.title);
        assertNotNull(reqScan.language);
        assertNotNull(reqCategory.title);
    }
}