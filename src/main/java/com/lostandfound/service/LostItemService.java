package com.lostandfound.service;

import com.lostandfound.exception.file.FileException;
import com.lostandfound.exception.file.FileNotFoundException;
import com.lostandfound.exception.file.UnSupportedFileFormatException;
import com.lostandfound.model.LostItem;
import com.lostandfound.repository.LostItemRepository;
import com.lostandfound.util.PdfReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LostItemService {

    private static final List<String> SUPPORTED_FORMAT = List.of("pdf");

    private final LostItemRepository lostItemRepository;

    public List<LostItem> getAllLostItems() {
        return lostItemRepository.findAll();
    }

    public void uploadLostItems(MultipartFile file) {
        validatedFile(file);
        File fileToUpload = convertMultipartFileToFile(file);
        List<LostItem> lostItems = PdfReader.parsePdf(fileToUpload);
        //TODO check if we need this and how to test?
        //deleting the temp file after use
        fileToUpload.delete();

        //storing lostItems in the DB
        lostItems.forEach(this :: saveLostItem);
    }
    private void saveLostItem(LostItem lostItem) {
        lostItemRepository.save(lostItem);
    }


    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        File convertedFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        try {
            multipartFile.transferTo(convertedFile);
        } catch (IOException e) {
            log.error("Error while reading multipart file");
            throw new FileException("Error while reading the input file");
        }
        return convertedFile;
    }

    private void validatedFile(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("No file found in the request");
            throw new FileNotFoundException("No file found in the request");
        }
        final String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (!SUPPORTED_FORMAT.contains(extension)) {
            log.error("Requested file format not supported.");
            throw new UnSupportedFileFormatException(
                    String.format("Unsupported file: %s format not supported.", extension));
        }
    }

}
