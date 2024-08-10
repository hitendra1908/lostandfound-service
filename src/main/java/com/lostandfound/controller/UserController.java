package com.lostandfound.controller;

import com.lostandfound.model.LostItem;
import com.lostandfound.service.ClaimedItemService;
import com.lostandfound.service.LostItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Operations related to user functions")
public class UserController {

    private final LostItemService lostItemService;

    private final ClaimedItemService claimedItemService;

    @Operation(summary = "Get all lost items", description = "Retrieve a list of all lost items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/lost-items")
    public List<LostItem> getAllLostItems() {
        return lostItemService.getAllLostItems();
    }

    @Operation(summary = "Claim a lost item", description = "Claim a lost item by specifying item ID and quantity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item claimed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/claim")
    public ResponseEntity<String> claimItem(@RequestParam Long lostItemId, @RequestParam int quantity, @RequestParam Long userId) {
        claimedItemService.claimItem(lostItemId, quantity, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Item claimed successfully");
    }

}
