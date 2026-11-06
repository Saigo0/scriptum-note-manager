package br.com.scriptum.service;

import br.com.scriptum.configuracao.UnitTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
public class DocumentServiceTest {

    private final DocumentService service = new DocumentService();

    @Test
    public void shouldThrowExceptionWhenTextExceeds2MB() {
        
        String hugeText = "*".repeat(2_100_000); 
        List<String> paragraphs = List.of(hugeText);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> service.createManualDocument("Title", paragraphs, 1L)
        );

        // Conferindo com a mensagem de erro que traduzimos
        assertEquals("The document text exceeds the 2MB limit.", exception.getMessage());
    }
}