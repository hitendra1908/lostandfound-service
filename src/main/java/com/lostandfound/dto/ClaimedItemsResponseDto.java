package com.lostandfound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimedItemsResponseDto {
    private long userId;
    private String name;
    List<ItemWithClaimedQuantityDto> claimedItems;
}
