package com.lostandfound.service;

import com.lostandfound.dto.ClaimedItemsResponseDto;
import com.lostandfound.dto.ItemWithClaimedQuantityDto;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ClaimedItemService {

    private final ClaimedItemRepository claimedItemRepository;

    private final LostItemRepository lostItemRepository;

    private final MockUserService mockUserService;

    public void claimItem(Long lostItemId, int quantity, Long userId) {
        LostItem lostItem = lostItemRepository.findById(lostItemId)
                .orElseThrow( () -> new ClaimItemNotFoundException("Item you are trying to claim is not found"));

        if (!mockUserService.userExists(userId)) {
            throw new ClaimingUserNotFoundException("User with +"+userId+" trying to claim the item not found");
        }
        if(quantity > lostItem.getQuantity()) {
            throw new ClaimQuantityException("Can not claim more than actual missing items");
        }
        ClaimedItem claimedItem = ClaimedItem.builder()
                        .lostItem(lostItem)
                        .claimedQuantity(quantity)
                        .userId(userId)
                        .build();

        claimedItemRepository.save(claimedItem);
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
