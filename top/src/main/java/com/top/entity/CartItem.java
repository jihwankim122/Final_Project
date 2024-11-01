package com.top.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="cart_item")

public class CartItem extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

     //241029 은열추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 
    private Member member;

    private int count;

      //241029 은열추가
      private int discount;
      private int totalPrice;
      private int finalPrice;

    public static CartItem createCartItem(Cart cart, Item item, int count, Member member){
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        cartItem.setMember(member);
        return cartItem;
    }

     // 가격 계산 메서드 241029 은열 추가
     public void calculatePrices(int discount, int totalPrice) {
        this.discount = discount;
        this.totalPrice = totalPrice;
        this.finalPrice = totalPrice - discount;
    }

    public void addCount(int count){
        this.count += count;
    }

    public void updateCount(int count){
        this.count = count;
    }
}
