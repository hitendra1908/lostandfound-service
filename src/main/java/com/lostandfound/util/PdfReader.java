package com.lostandfound.util;

import com.lostandfound.model.LostItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PdfReader {

    public static List<LostItem> parsePdf(File file) {
        List<LostItem> lostItems = new ArrayList<>();
        try {
            PDDocument document = PDDocument.load(file);

            // Extracting text from document
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            document.close();

            // Process the extracted text
            lostItems =  parseText(text);

        } catch (IOException e) {
            e.printStackTrace(); // TODO throw business exception
        }
        log.info("{} valid lost items found in {} file",lostItems.size(),file.getName() );
        return lostItems;
    }

    static String itemName = "";
    static String quantityStr = "";
    static String place = "";
    static boolean isInvalidRecord = false;

    private static List<LostItem> parseText(String text) {
        List<LostItem> lostItems = new ArrayList<>();
        // Split the text into lines
        String[] lines = text.split("\\r?\\n");

        for (String line : lines) {
            line = line.trim();

            // Identify and extract ItemName
            if (line.startsWith("ItemName:")) {
                itemName = line.substring("ItemName:".length()).trim();
                if(itemName.isEmpty()) {
                    isInvalidRecord =true;
                }
            }
            // Identify and extract Quantity
            else if (line.startsWith("Quantity:")) {
                quantityStr = line.substring("Quantity:".length()).trim();
                try {
                    Integer.parseInt(quantityStr);
                } catch (NumberFormatException exception) {
                    isInvalidRecord = true;
                }
            }
            // Identify and extract Place
            else if (line.startsWith("Place:")) {
                place = line.substring("Place:".length()).trim();
                if(place.isEmpty()) {
                    isInvalidRecord =true;
                }
            }

            if (isInvalidRecord) {
                itemName = "";
                quantityStr = "";
                place = "";
            }
            // When a complete entry is found, store it in db and reset variables
            if (!itemName.isEmpty() && !quantityStr.isEmpty() && !place.isEmpty()) {

                createLostItemObj(itemName, quantityStr, place, lostItems);

                resetFlags();
            }
        }
        return lostItems;
    }

    private static void resetFlags() {
        // Reset the variables for the next entry
        itemName = "";
        quantityStr = "";
        place = "";
        isInvalidRecord = false;
    }

    private static void createLostItemObj(String itemName, String quantityStr, String place, List<LostItem> lostItems) {
        LostItem item = LostItem.builder()
                .itemName(itemName)
                .quantity(Integer.parseInt(quantityStr))
                .place(place)
                .build();
        lostItems.add(item);
    }
}
