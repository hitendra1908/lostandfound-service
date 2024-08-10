package com.lostandfound.dto;

import com.lostandfound.model.LostItem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimLostItemRequestDto {
    private long userId;
    private int quantity;
    private long lostItemId;
}
