package com.lostandfound.dto;

import com.lostandfound.model.LostItem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemWithClaimedQuantityDto {
    private int claimedQuantity;
    private LostItem lostItem;
}
