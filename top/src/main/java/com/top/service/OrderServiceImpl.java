package com.top.service;

import com.top.dto.OrderDto;
import com.top.entity.*;
import com.top.repository.ItemRepository;
import com.top.repository.MemberRepository;
import com.top.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import com.top.dto.OrderHistDto;
import com.top.dto.OrderItemDto;
import com.top.repository.ItemImgRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    @Override
    public Long order(OrderDto orderDto, String email) {
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<>(orderHistDtos, pageable, totalCount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        return StringUtils.equals(curMember.getEmail(), savedMember.getEmail());
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    @Override
    public Long orders(List<OrderDto> orderDtoList, String email) {
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }


    // 관리자용 구매 이력 페이지
    @Override
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderListByAdmin(Pageable pageable) {
        List<Order> orders = orderRepository.findOrdersByAdmin(pageable); // 페이징 조건을 이용하여 전체 주문 목록 조회
        Long totalCount = orderRepository.countOrderByAdmin(); // 총 주문 개수 구하기

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) { // 주문 리스트를 순회하면서 구매 이력 페이지에 전달할 DTO 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");  // 주문한 상품의 대표 이미지 조회
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<>(orderHistDtos, pageable, totalCount); // 페이지 구현 객체를 생성하여 반환
    }
}
