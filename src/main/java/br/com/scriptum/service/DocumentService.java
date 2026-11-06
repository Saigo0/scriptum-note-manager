package br.com.scriptum.service;

import br.com.scriptum.dto.OcrRequest;
import br.com.scriptum.model.Document;
import br.com.scriptum.model.Category;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.nio.charset.StandardCharsets;
import java.util.List;

@ApplicationScoped
public class DocumentService {

    @Inject
    @Channel("ocr-processing")
    Emitter<OcrRequest> ocrEmitter;

    private void validateSize(List<String> paragraphs) {
        if (paragraphs == null || paragraphs.isEmpty()) return;
        
        long sizeInBytes = paragraphs.stream()
                .mapToLong(p -> p.getBytes(StandardCharsets.UTF_8).length)
                .sum();
                
        if (sizeInBytes > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("The document text exceeds the 2MB limit.");
        }
    }

    @Transactional
    public List<Document> findByCategoryId(Long categoryId) {
        return Document.find("category.id", categoryId).list();
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

    public Document findById(Long id) {
        return Document.findById(id);
    }

    @Transactional
    public Document createScannedDocument(Long categoryId, String title, String language, String imageBase64) {
        Category category = Category.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }

        Document doc = new Document();
        doc.title = title;
        doc.status = "PROCESSING_OCR"; // Status temporário
        doc.category = category; // Vínculo essencial
        doc.persist(); 
        
        // Agora que o documento tem um ID gerado pelo banco, você emite para a fila
        OcrRequest ocrRequest = new OcrRequest(doc.id, language, imageBase64);
        // Seu código que envia o ocrRequest para o RabbitMQ (ex: emitter.send(ocrRequest))
        
        return doc;
    }

    @Transactional
    public void processOcrReturn(Long id, List<String> paragraphs, Double reliability) {
        Document doc = Document.findById(id);
        if (doc != null) {
            try {
                validateSize(paragraphs);
                doc.content = paragraphs;
                doc.reliability = reliability;
                doc.status = "SCANNED";
            } catch (IllegalArgumentException e) {
                doc.status = "SIZE_LIMIT_ERROR";
            }
        }
    }

    @Transactional
    public Document editDocument(Long id, List<String> newParagraphs) {
        Document doc = Document.findById(id);
        if (doc != null) {
            validateSize(newParagraphs);
            doc.content = newParagraphs;
            doc.status = "MANUALLY_TYPED";
        }
        return doc;
    }
}