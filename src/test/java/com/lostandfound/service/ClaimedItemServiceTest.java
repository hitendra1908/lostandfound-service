package com.lostandfound.service;

import com.lostandfound.client.UserServiceRestClient;
import com.lostandfound.dto.ClaimedItemsResponseDto;
import com.lostandfound.exception.claim.ClaimException;
import com.lostandfound.exception.claim.ClaimItemNotFoundException;
import com.lostandfound.exception.claim.ClaimQuantityException;
import com.lostandfound.exception.claim.ClaimingUserNotFoundException;
import com.lostandfound.model.ClaimedItem;
import com.lostandfound.model.LostItem;
import com.lostandfound.repository.ClaimedItemRepository;
import com.lostandfound.repository.LostItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimedItemServiceTest {

    @Mock
    private ClaimedItemRepository claimedItemRepository;

    @Mock
    private LostItemRepository lostItemRepository;

    @Mock
    private UserServiceRestClient userServiceRestClient;

    @InjectMocks
    private ClaimedItemService claimedItemService;

    private LostItem lostItem;

    @BeforeEach
    void setup() {
        lostItem = LostItem.builder()
                .id(1L)
                .itemName("Watch")
                .quantity(5)
                .place("Park")
                .build();
    }

    @Test
    void shouldClaimItem_whenValidRequest() {
        Long lostItemId = 1L;
        int claimQuantity = 2;
        Long userId = 3L;

        when(lostItemRepository.findById(lostItemId)).thenReturn(Optional.of(lostItem));
        when(userServiceRestClient.getNameById(userId)).thenReturn("User1");

        claimedItemService.claimItem(lostItemId, claimQuantity, userId);

        ArgumentCaptor<ClaimedItem> claimedItemCaptor = ArgumentCaptor.forClass(ClaimedItem.class);
        verify(claimedItemRepository).save(claimedItemCaptor.capture());
        ClaimedItem savedClaimedItem = claimedItemCaptor.getValue();

        assertEquals(lostItem, savedClaimedItem.getLostItem());
        assertEquals(claimQuantity, savedClaimedItem.getClaimedQuantity());
        assertEquals(userId, savedClaimedItem.getUserId());
    }

    @Test
    void shouldThrowClaimItemNotFoundException_whenItemNotFound() {
        Long lostItemId = 1L;
        int quantity = 2;
        Long userId = 3L;

        when(lostItemRepository.findById(lostItemId)).thenReturn(Optional.empty());

        ClaimItemNotFoundException exception = assertThrows(ClaimItemNotFoundException.class, () ->
                claimedItemService.claimItem(lostItemId, quantity, userId));

        assertEquals("Item you are trying to claim is not found", exception.getMessage());
    }

    @Test
    void shouldThrowClaimingUserNotFoundException_whenUserNotFound() {
        Long lostItemId = 1L;
        int quantity = 2;
        Long userId = 3L;

        when(lostItemRepository.findById(lostItemId)).thenReturn(Optional.of(lostItem));
        when(userServiceRestClient.getNameById(userId)).thenThrow(new ClaimingUserNotFoundException("user not found"));

        ClaimingUserNotFoundException exception = assertThrows(ClaimingUserNotFoundException.class, () ->
                claimedItemService.claimItem(lostItemId, quantity, userId));

        assertEquals("user not found", exception.getMessage());
    }

    @Test
    void shouldThrowClaimQuantityException_whenClaimQuantityExceedsAvailable() {
        Long lostItemId = 1L;
        int quantity = 6;
        Long userId = 3L;

        when(lostItemRepository.findById(lostItemId)).thenReturn(Optional.of(lostItem));
        when(userServiceRestClient.getNameById(userId)).thenReturn("User1");

        ClaimQuantityException exception = assertThrows(ClaimQuantityException.class, () ->
                claimedItemService.claimItem(lostItemId, quantity, userId));

        assertEquals("Can't claim more than the actual missing items", exception.getMessage());
    }

    @Test
    void shouldThrowClaimItemException_whenItemAlreadyClaimedByUser() {
        Long lostItemId = 1L;
        int claimQuantity = 2;
        Long userId = 3L;

        when(lostItemRepository.findById(lostItemId)).thenReturn(Optional.of(lostItem));
        when(userServiceRestClient.getNameById(userId)).thenReturn("User1");

        claimedItemService.claimItem(lostItemId, claimQuantity, userId);

        ArgumentCaptor<ClaimedItem> claimedItemCaptor = ArgumentCaptor.forClass(ClaimedItem.class);
        verify(claimedItemRepository).save(claimedItemCaptor.capture());
        ClaimedItem savedClaimedItem = claimedItemCaptor.getValue();

        assertEquals(lostItem, savedClaimedItem.getLostItem());
        assertEquals(claimQuantity, savedClaimedItem.getClaimedQuantity());
        assertEquals(userId, savedClaimedItem.getUserId());

        //claiming again same item
        ClaimedItem claimedItem = ClaimedItem.builder()
                .lostItem(lostItem)
                .claimedQuantity(2)
                .userId(userId)
                .build();

        when(claimedItemRepository.findByUserId(userId)).thenReturn(List.of(claimedItem));

        ClaimException exception = assertThrows(ClaimException.class, () ->
                claimedItemService.claimItem(lostItemId, claimQuantity, userId));

        assertEquals("Item with this ID is already claimed by user: 3", exception.getMessage());
    }

    @Test
    void shouldGetAllClaimedItems() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        LostItem lostItemLaptop = LostItem.builder()
                .id(1L)
                .itemName("Laptop")
                .quantity(5)
                .place("Bus")
                .build();

        ClaimedItem claimedItem1 = ClaimedItem.builder()
                .lostItem(lostItem)
                .claimedQuantity(2)
                .userId(userId1)
                .build();

        ClaimedItem claimedItem2 = ClaimedItem.builder()
                .lostItem(lostItemLaptop)
                .claimedQuantity(2)
                .userId(userId1)
                .build();

        ClaimedItem claimedItem3 = ClaimedItem.builder()
                .lostItem(lostItem)
                .claimedQuantity(2)
                .userId(userId2)
                .build();

        when(claimedItemRepository.findAll()).thenReturn(Arrays.asList(claimedItem1, claimedItem2, claimedItem3));
        when(userServiceRestClient.getNameById(userId1)).thenReturn("User1");
        when(userServiceRestClient.getNameById(userId2)).thenReturn("User2");

        List<ClaimedItemsResponseDto> result = claimedItemService.getAllClaimedItems();

        assertEquals(2, result.size());

        ClaimedItemsResponseDto user1Dto = result.getFirst();
        assertEquals(userId1, user1Dto.getUserId());
        assertEquals("User1", user1Dto.getName());
        assertEquals(2, user1Dto.getClaimedItems().size());
        assertEquals(lostItem, user1Dto.getClaimedItems().getFirst().getLostItem());
        assertEquals(lostItemLaptop, user1Dto.getClaimedItems().get(1).getLostItem());

        ClaimedItemsResponseDto user2Dto = result.get(1);
        assertEquals(userId2, user2Dto.getUserId());
        assertEquals("User2", user2Dto.getName());
        assertEquals(1, user2Dto.getClaimedItems().size());
        assertEquals(lostItem, user1Dto.getClaimedItems().getFirst().getLostItem());
    }
}
