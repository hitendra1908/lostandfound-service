package com.lostandfound.service;

import com.lostandfound.dto.ClaimedItemsResponseDto;
import com.lostandfound.dto.ItemWithClaimedQuantityDto;
import com.lostandfound.exception.claim.ClaimException;
import com.lostandfound.exception.claim.ClaimItemNotFoundException;
import com.lostandfound.exception.claim.ClaimQuantityException;
import com.lostandfound.exception.claim.ClaimingUserNotFoundException;
import com.lostandfound.model.ClaimedItem;
import com.lostandfound.model.LostItem;
import com.lostandfound.repository.ClaimedItemRepository;
import com.lostandfound.repository.LostItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ClaimedItemService {

    private final ClaimedItemRepository claimedItemRepository;

    private final LostItemRepository lostItemRepository;

    private final MockUserService mockUserService;

    public void claimItem(Long lostItemId, int quantity, Long userId) {
        LostItem lostItem = findLostItem(lostItemId);
        validateUser(userId);
        validateClaimQuantity(quantity, lostItem);
        checkIfItemAlreadyClaimedBySameUser(lostItemId, quantity, userId);

        saveClaimedItem(lostItem, quantity, userId);
    }

    public List<ClaimedItemsResponseDto> getAllClaimedItems() {
        List<ClaimedItem> claimedItems = claimedItemRepository.findAll();
        return claimedItems.stream()
                .collect(Collectors.groupingBy(ClaimedItem::getUserId))
                .entrySet()
                .stream()
                .map(entry -> createClaimedItemsResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private LostItem findLostItem(Long lostItemId) {
        return lostItemRepository.findById(lostItemId)
                .orElseThrow(() -> new ClaimItemNotFoundException("Item you are trying to claim is not found"));
    }

    private void validateUser(Long userId) {
        if (!mockUserService.userExists(userId)) {
            throw new ClaimingUserNotFoundException("User with id " + userId + " is not found");
        }
    }

    private void validateClaimQuantity(int quantity, LostItem lostItem) {
        if (quantity > lostItem.getQuantity()) {
            throw new ClaimQuantityException("Can't claim more than the actual missing items");
        }
    }

    private void checkIfItemAlreadyClaimedBySameUser(Long lostItemId, int quantity, Long userId) {
        claimedItemRepository.findByUserId(userId)
                .stream()
                .filter(claimedItem -> isAlreadyClaimedByUser(claimedItem, lostItemId, quantity))
                .findFirst()
                .ifPresent(claimedItem -> {
                    throw new ClaimException("Item with this ID is already claimed by user: " + userId);
                });
    }

    private boolean isAlreadyClaimedByUser(ClaimedItem claimedItem, Long lostItemId, int quantity) {
        return Objects.equals(lostItemId, claimedItem.getLostItem().getId()) && quantity == claimedItem.getClaimedQuantity();
    }

    private void saveClaimedItem(LostItem lostItem, int quantity, Long userId) {
        ClaimedItem claimedItem = ClaimedItem.builder()
                .lostItem(lostItem)
                .claimedQuantity(quantity)
                .userId(userId)
                .build();

        claimedItemRepository.save(claimedItem);
    }

    private ClaimedItemsResponseDto createClaimedItemsResponse(Long userId, List<ClaimedItem> claimedItems) {
        String userName = mockUserService.getUserNameById(userId);
        List<ItemWithClaimedQuantityDto> itemWithClaimedQuantityList = claimedItems.stream()
                .map(this::mapToItemWithClaimedQuantityDto)
                .collect(Collectors.toList());

        return ClaimedItemsResponseDto.builder()
                .userId(userId)
                .userName(userName)
                .claimedItems(itemWithClaimedQuantityList)
                .build();
    }

    private ItemWithClaimedQuantityDto mapToItemWithClaimedQuantityDto(ClaimedItem item) {
        return ItemWithClaimedQuantityDto.builder()
                .claimedQuantity(item.getClaimedQuantity())
                .lostItem(item.getLostItem())
                .build();
    }

}
