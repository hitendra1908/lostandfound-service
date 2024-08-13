package com.lostandfound.controller;

import com.lostandfound.dto.ClaimedItemsResponseDto;
import com.lostandfound.dto.UserRequestDto;
import com.lostandfound.model.User;
import com.lostandfound.service.ClaimedItemService;
import com.lostandfound.service.LostItemService;
import com.lostandfound.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    private final UserService userService;

    @Operation(summary = "Upload lost items", description = "Upload a file containing lost items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "File uploaded and processed successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Invalid file uploaded"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadLostItems(@RequestParam("file") MultipartFile file) {
        String uploadMessage = lostItemService.uploadLostItems(file);
        return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded: "+uploadMessage);
    }

    @Operation(summary = "Get all claimed items", description = "Retrieve a list of all claimed items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/claimed-items")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ClaimedItemsResponseDto>> getAllClaimedItems() {
        List<ClaimedItemsResponseDto> result = claimedItemService.getAllClaimedItems();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get all Users", description = "Retrieve a list of all Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of Users"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> result = userService.getAllUsers();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Create a new User", description = "To create new User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created a User"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/users",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody UserRequestDto incomingUser) {
        User savedUser = userService.saveUser(incomingUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}
