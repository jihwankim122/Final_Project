package com.top.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class WishlistItemDto {

        @NotNull(message = "상품 아이디를 입력하세요")
        private Long itemId;
    }
