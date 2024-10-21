package com.top.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {

    @NotNull(message = "상품 아이디를 입력하세요")
    private Long itemId;

    @Min(value = 1, message = "물건을 1개 이상 담아주세요")
    private int count;
}
