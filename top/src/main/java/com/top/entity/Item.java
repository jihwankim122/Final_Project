package com.top.entity;

import com.top.constant.ItemSellStatus;
import com.top.exception.OutOfStockException;
import lombok.*;

import jakarta.persistence.*;
import com.top.dto.ItemFormDto;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Item extends BaseEntity {

    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 1101 성아 id-> no 변경

    @Column(nullable = false)
    private Long category;// 241022 은열 추가

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(name="price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    @Builder
    public Item(Long no, Long category, String itemNm, int price, int stockNumber, String itemDetail, ItemSellStatus itemSellStatus) {
        this.no = no; // 1101 성아 id -> no 수정
        this.category = category;
        this.itemNm = itemNm;
        this.price = price;
        this.stockNumber = stockNumber;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
    }

    public void updateItem(ItemFormDto itemFormDto){
        this.category=itemFormDto.getCategory();// 241022 은열 추가
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock<0){
            throw new OutOfStockException("OutOfStock " +
                    "(quantity: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;

        // 1108 성아 추가
        // 재고가 0이면 itemSellStatus를 SOLD_OUT으로 설정
        updateItemSellStatus();
    }

    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }

    // 1108 성아 추가
    // 재고가 0일 때 itemSellStatus를 SOLD_OUT으로 변경하는 메서드
    public void updateItemSellStatus() {
        if (this.stockNumber == 0) {
            this.itemSellStatus = ItemSellStatus.SOLD_OUT;
        }
    }

}