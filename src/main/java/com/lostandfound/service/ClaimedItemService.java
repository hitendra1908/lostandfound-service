package com.lostandfound.service;

import com.lostandfound.model.ClaimedItem;
import com.lostandfound.model.LostItem;
import com.lostandfound.repository.ClaimedItemRepository;
import com.lostandfound.repository.LostItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClaimedItemService {

    private final ClaimedItemRepository claimedItemRepository;

    private final LostItemRepository lostItemRepository;

    private final MockUserService mockUserService;

    public void claimItem(Long lostItemId, int quantity, Long userId) {
        LostItem lostItem = lostItemRepository.findById(lostItemId)
                .orElseThrow( () -> new RuntimeException("Item not found")); //TODO throw business error

        if (!mockUserService.userExists(userId)) {
            throw new RuntimeException("User not present "); //TODO buisness excpetion
        }
        ClaimedItem claimedItem = new ClaimedItem();
        claimedItem.setLostItem(lostItem);
        claimedItem.setClaimedQuantity(quantity);
        claimedItem.setUserId(userId);
        claimedItemRepository.save(claimedItem);
    }

    public List<ClaimedItem> getAllClaimedItems() {
        return claimedItemRepository.findAll();
    }
}
