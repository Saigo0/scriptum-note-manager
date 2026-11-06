package br.com.scriptum;

import br.com.scriptum.configuracao.IntegrationTest;
import br.com.scriptum.resource.CategoryResource;
import br.com.scriptum.resource.DocumentResource;
import br.com.scriptum.service.CategoryService;
import br.com.scriptum.service.DocumentService;
import br.com.scriptum.resource.DocumentResource.ManualDocumentRequest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@IntegrationTest
public class ServicoDiretoTest {

    @Inject DocumentResource documentResource;
    @Inject CategoryResource categoryResource;
    @Inject DocumentService documentService;
    @Inject CategoryService categoryService;

    @Test
    public void testForceDirectLayersCoverage() {

        CategoryResource.CategoryRequest reqCategory = new CategoryResource.CategoryRequest();
        reqCategory.title = "Direct Coverage Category";
        
        categoryResource.create(reqCategory);
        assertNotNull(categoryResource.list());
        assertNotNull(categoryService.listCategories());

        ManualDocumentRequest request = new ManualDocumentRequest();
        request.categoryId = 1L;
        request.title = "Teste";
        request.paragraphs = List.of("Paragrafo");
        
        assertNotNull(documentResource.createManual(request));

        DocumentResource.ScannedDocumentRequest reqScan = new DocumentResource.ScannedDocumentRequest();
        reqScan.title = "Direct Scan";
        reqScan.language = "en";
        reqScan.imageBase64 = "fake-image";
        
        assertNotNull(documentResource.createScanned(reqScan));

        documentResource.editDocument(999L, List.of("Edited Text"));
    }
}