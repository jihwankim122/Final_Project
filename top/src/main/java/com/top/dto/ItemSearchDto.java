package com.top.dto;

import com.top.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {
    private String searchDateType;
    private Long category;
    private ItemSellStatus searchSellStatus;
    private String searchBy;
    private String searchQuery="";
}
