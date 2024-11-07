package com.top.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistDetailDto {
    private Long wishlistItemId;
    private String itemNm;
    private int price;
    private String imgUrl;

    public WishlistDetailDto(Long wishlistItemId, String itemNm, int price, String imgUrl) {
        this.wishlistItemId = wishlistItemId;
        this.itemNm = itemNm;
        this.price = price;
        this.imgUrl = imgUrl;
    }
}
