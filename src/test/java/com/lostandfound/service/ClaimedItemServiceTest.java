package com.lostandfound.service;

import com.lostandfound.dto.ClaimedItemsResponseDto;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimedItemServiceTest {

    @Mock
    private ClaimedItemRepository claimedItemRepository;

    @Mock
    private LostItemRepository lostItemRepository;

    @Mock
    private MockUserService mockUserService;

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
        when(mockUserService.userExists(userId)).thenReturn(true);

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
        when(mockUserService.userExists(userId)).thenReturn(false);

        ClaimingUserNotFoundException exception = assertThrows(ClaimingUserNotFoundException.class, () ->
                claimedItemService.claimItem(lostItemId, quantity, userId));

        assertEquals("User with +" + userId + " trying to claim the item not found", exception.getMessage());
    }

    @Test
    void shouldThrowClaimQuantityException_whenClaimQuantityExceedsAvailable() {
        Long lostItemId = 1L;
        int quantity = 6;
        Long userId = 3L;

        when(lostItemRepository.findById(lostItemId)).thenReturn(Optional.of(lostItem));
        when(mockUserService.userExists(userId)).thenReturn(true);

        ClaimQuantityException exception = assertThrows(ClaimQuantityException.class, () ->
                claimedItemService.claimItem(lostItemId, quantity, userId));

        assertEquals("Can not claim more than actual missing items", exception.getMessage());
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
        when(mockUserService.getUserNameById(userId1)).thenReturn("User1");
        when(mockUserService.getUserNameById(userId2)).thenReturn("User2");

        List<ClaimedItemsResponseDto> result = claimedItemService.getAllClaimedItems();

        assertEquals(2, result.size());

        ClaimedItemsResponseDto user1Dto = result.getFirst();
        assertEquals(userId1, user1Dto.getUserId());
        assertEquals("User1", user1Dto.getUserName());
        assertEquals(2, user1Dto.getClaimedItems().size());
        assertEquals(lostItem, user1Dto.getClaimedItems().getFirst().getLostItem());
        assertEquals(lostItemLaptop, user1Dto.getClaimedItems().get(1).getLostItem());

        ClaimedItemsResponseDto user2Dto = result.get(1);
        assertEquals(userId2, user2Dto.getUserId());
        assertEquals("User2", user2Dto.getUserName());
        assertEquals(1, user2Dto.getClaimedItems().size());
        assertEquals(lostItem, user1Dto.getClaimedItems().getFirst().getLostItem());
    }
}
