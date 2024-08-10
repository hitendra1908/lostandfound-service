package com.lostandfound.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClaimedItemsResponseDto {
    private long userId;
    private String userName;
    List<ItemWithClaimedQuantityDto> claimedItems;
}
