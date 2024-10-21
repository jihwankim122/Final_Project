package com.top.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {

    @NotNull(message = "상품 아이디를 입력하세요")
    private Long itemId;

    @Min(value = 1, message = "1개 이상 주문해주세요")
    @Max(value = 999, message = "999개 이하로 주문해주세요")
    private int count;
}
