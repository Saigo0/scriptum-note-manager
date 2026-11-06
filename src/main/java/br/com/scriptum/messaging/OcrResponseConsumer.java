package br.com.scriptum.messaging;

import br.com.scriptum.dto.OcrResponse;
import br.com.scriptum.service.DocumentService;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OcrResponseConsumer {

    private static final Logger LOG = Logger.getLogger(OcrResponseConsumer.class);

    @Inject
    DocumentService documentService;

    @Incoming("ocr-reading-return")
    public void processOcrResult(String payload) { 
        LOG.info("Receiving OCR result via RabbitMQ");
        try {
            JsonObject json = new JsonObject(payload);
            OcrResponse response = json.mapTo(OcrResponse.class);
            
            documentService.processOcrReturn(
                response.documentId, 
                response.paragraphs, 
                response.confidence
            );
            
            LOG.infof("Document %d successfully updated by OCR.", response.documentId);
        } catch (Exception e) {
            LOG.error("Failed to process OCR return", e);
        }
    }
}