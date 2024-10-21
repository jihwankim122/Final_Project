package com.top.service;

import com.top.dto.CartItemDto;
import com.top.dto.CartDetailDto;
import com.top.dto.CartOrderDto;
import com.top.dto.OrderDto;

import java.util.List;

public interface CartService {
    Long addCart(CartItemDto cartItemDto, String email);
    List<CartDetailDto> getCartList(String email);
    boolean validateCartItem(Long cartItemId, String email);
    void updateCartItemCount(Long cartItemId, int count);
    void deleteCartItem(Long cartItemId);
    Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email);
    void deleteCartItemByItemId(Long itemId);
}
