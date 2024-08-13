package com.lostandfound.service;

import com.lostandfound.exception.file.FileException;
import com.lostandfound.exception.file.FileNotFoundException;
import com.lostandfound.exception.file.UnSupportedFileFormatException;
import com.lostandfound.model.LostItem;
import com.lostandfound.repository.LostItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LostItemServiceTest {

    @Mock
    private LostItemRepository lostItemRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private LostItemService lostItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllLostItems() {
        List<LostItem> lostItems = List.of(new LostItem(), new LostItem());
        when(lostItemRepository.findAll()).thenReturn(lostItems);

        List<LostItem> result = lostItemService.getAllLostItems();

        assertEquals(2, result.size());
        verify(lostItemRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowFileNotFoundException_WhenFileIsEmpty() {
        when(multipartFile.isEmpty()).thenReturn(true);

        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> {
            lostItemService.uploadLostItems(multipartFile);
        });

        assertEquals("No file found in the request", exception.getMessage());
        verify(lostItemRepository, never()).save(any(LostItem.class));
    }

    @Test
    void shouldUploadLostItems_UnSupportedFileFormat() {
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.isEmpty()).thenReturn(false);

        UnSupportedFileFormatException exception = assertThrows(UnSupportedFileFormatException.class, () -> {
            lostItemService.uploadLostItems(multipartFile);
        });

        assertEquals("Unsupported file: txt format not supported.", exception.getMessage());
        verify(lostItemRepository, never()).save(any(LostItem.class));
    }

    @Test
    void shouldThrowFileException_whenMultipartFileTransferToGivesIOException() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        doThrow(IOException.class).when(multipartFile).transferTo(any(File.class));

        FileException exception = assertThrows(FileException.class, () -> {
            lostItemService.uploadLostItems(multipartFile);
        });

        assertEquals("Error while reading the input file", exception.getMessage());
    }

}
