package com.top.service;

import com.top.dto.WishlistItemDto;
import com.top.dto.WishlistDetailDto;


import java.util.List;

public interface WishlistService {
    Long addWishlist(WishlistItemDto wishlistItemDto, String email);
    List<WishlistDetailDto> getWishlist(String email);
    boolean validateWishlistItem(Long wishlistItemId, String email);
    void deleteWishlistItem(Long wishlistItemId);
    void deleteWishlistItemByItemId(Long itemId);
}
