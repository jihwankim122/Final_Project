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
    }


    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;
    private Member member; // 추가

    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    public void addOrderItemDto(OrderItemDto orderItemDto){
        orderItemDtoList.add(orderItemDto);
    }
}
