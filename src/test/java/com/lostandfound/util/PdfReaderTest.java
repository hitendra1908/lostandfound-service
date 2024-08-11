package com.lostandfound.util;

import com.lostandfound.exception.file.FileException;
import com.lostandfound.model.LostItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class PdfReaderTest {

    @Test
    void shouldParsePdfSuccessfully_WhenValidFile() {
        String resourceName = "SampleFile_LostAndFound.pdf";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());

        List<LostItem> result = PdfReader.parsePdf(file);

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("Laptop", result.get(0).getItemName());
        assertEquals(1, result.get(0).getQuantity());
        assertEquals("Taxi", result.get(0).getPlace());

    }

    @Test
    void shouldThrowFileException_WhenIOException() {
        File file = mock(File.class);

        try (MockedStatic<PDDocument> pdDocumentMockedStatic = Mockito.mockStatic(PDDocument.class)) {
            pdDocumentMockedStatic.when(() -> PDDocument.load(file)).thenThrow(new IOException());

            FileException exception = assertThrows(FileException.class, () -> PdfReader.parsePdf(file));
            assertEquals("Exception occurred while processing the file", exception.getMessage());
        }
    }

    @Test
    void shouldParseOnlyValidRecord_WhenFileHasFewInvalidRecords() {
        String resourceName = "Invalid_SampleFile_LostAndFound.pdf";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());

        List<LostItem> result = PdfReader.parsePdf(file);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getItemName());
        assertEquals(1, result.get(0).getQuantity());
        assertEquals("Taxi", result.get(0).getPlace());
    }
}
