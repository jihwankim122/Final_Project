package com.top.service;

import com.top.dto.OrderDto;
import com.top.dto.OrderHistDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    Long order(OrderDto orderDto, String email);
    Page<OrderHistDto> getOrderList(String email, Pageable pageable);
    boolean validateOrder(Long orderId, String email);
    void cancelOrder(Long orderId);
    Long orders(List<OrderDto> orderDtoList, String email);

    Page<OrderHistDto> getOrderListByAdmin(Pageable pageable);

    void requestCancelOrder(Long orderId); // 주문 취소 요청
}
