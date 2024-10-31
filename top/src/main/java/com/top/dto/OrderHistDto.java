package com.top.dto;

import com.top.constant.OrderStatus;
import com.top.entity.Member;
import com.top.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderHistDto {

    public OrderHistDto(Order order){
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
        this.member = order.getMember(); // 추가
        //241028은열 추가
        this.totalPrice = order.getTotalPrice(); 
        this.finalPrice = order.getFinalPrice(); 
        this.discount = order.getDiscount(); 
    }


    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;
    private Member member; // 추가
    //241028 은열추가
    private int totalPrice;
    private int discount;
    private int finalPrice;

    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    public void addOrderItemDto(OrderItemDto orderItemDto){
        orderItemDtoList.add(orderItemDto);
    }
}
