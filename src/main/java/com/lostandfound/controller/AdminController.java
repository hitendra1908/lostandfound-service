package com.lostandfound.controller;

import com.lostandfound.dto.ClaimedItemsResponseDto;
import com.lostandfound.service.ClaimedItemService;
import com.lostandfound.service.LostItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Controller", description = "Operations related to admin functions")
@RequiredArgsConstructor
public class AdminController {

    private final LostItemService lostItemService;

    private final ClaimedItemService claimedItemService;

    @Operation(summary = "Upload lost items", description = "Upload a file containing lost items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "File uploaded and processed successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Invalid file uploaded"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadLostItems(@RequestParam("file") MultipartFile file) {
        lostItemService.uploadLostItems(file);
        return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded and processed successfully");
    }

    @Operation(summary = "Get all claimed items", description = "Retrieve a list of all claimed items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/claimed-items")
    public ResponseEntity<List<ClaimedItemsResponseDto>> getAllClaimedItems() {
        List<ClaimedItemsResponseDto> result = claimedItemService.getAllClaimedItems();
        return ResponseEntity.ok(result);
    }
}
