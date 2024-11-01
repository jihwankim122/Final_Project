package com.top.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.top.constant.ItemSellStatus;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MainItemDto {

    private Long no; // 1101 성아 id -> no 수정

    private Long category; //241022 은열 추가

    private ItemSellStatus itemSellStatus; // 1018 은열 추가

    private String itemNm;

    private String itemDetail;

    private String imgUrl;

    private Integer price;

    // 1101 성아 id -> no 수정
    @QueryProjection
    public MainItemDto(Long no, Long category, ItemSellStatus itemSellStatus, String itemNm, String itemDetail, String imgUrl, Integer price){
        this.no = no;
        this.category = category; // 은열추가
        this.itemSellStatus = itemSellStatus; // 은열추가
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }

}