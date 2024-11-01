package com.top.entity;

import com.top.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    //241029 은열 추가
    private int discount;

    private int finalPrice;

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList,int discount, int finalPrice){
        Order order = new Order();
        order.setMember(member);
        for(OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        //241028 은열 추가
        order.setDiscount(discount); // 할인되는 가격 적용
        order.setFinalPrice(finalPrice); // 최종 가격 적용

        //241101 은열 주문 가격을 누적가격에추가
        member.addOrderPrice(finalPrice); // 멤버의 누적 주문 금액 업데이트
        return order;
    }

    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    // 주문 취소 요청
    public void requestCancelOrder() {
        if (this.orderStatus == OrderStatus.ORDER) {
            this.orderStatus = OrderStatus.CANCEL_REQUEST;
        }
    }

    // 주문 취소 승인
    public void cancelOrder() {
        if (this.orderStatus == OrderStatus.CANCEL_REQUEST) { // if문 추가
            this.orderStatus = OrderStatus.CANCEL;
            for (OrderItem orderItem : orderItems) {
                orderItem.cancel();
            }
        }
    }

}
