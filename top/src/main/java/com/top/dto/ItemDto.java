package com.top.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemDto {
    private Long no; // 1101 성아 수정 id -> no
    private Long category; // 241022 은열추가
    private String itemNm;
    private Integer price;
    private String itemDetail;
    private String sellStatCd;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    // 영화의 평균 평점 // 1028 성아 추가
    private Double avg;

    // 리뷰 수 jpa의 count( ) // 1028 성아 추가
    private Integer reviewCnt;
}
