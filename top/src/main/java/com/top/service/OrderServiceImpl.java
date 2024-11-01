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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import com.top.security.dto.ClubAuthMemberDto;
import org.thymeleaf.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;
      //241028은열추가
    private final DiscountService discountService;

    // 주문
    //241028은열수정
    @Override
    public Long order(OrderDto orderDto, String email) {
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);
        int price = item.getPrice();
        int count = orderDto.getCount(); // 주문 수량 가져오기
        int totalPrice = price * count; // 총 가격 계산
        int discount = discountService.discount(member,totalPrice);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList,discount, totalPrice - discount);
        orderRepository.save(order);

        return order.getId();
    }

    // 로그인한 사용자의 이메일 가져오기 (소셜/일반 로그인 모두 처리)
    private String getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("로그인된 사용자가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getUsername();  // 일반 로그인 사용자의 이메일 반환
        } else if (principal instanceof ClubAuthMemberDto) {
            return ((ClubAuthMemberDto) principal).getEmail();  // 소셜 로그인 사용자의 이메일 반환
        } else {
            throw new IllegalStateException("알 수 없는 사용자 타입입니다.");
        }
    }

    // 주문 목록 조회
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
                ItemImg itemImg = itemImgRepository.findByItemNoAndRepimgYn(orderItem.getItem().getNo(), "Y"); // 1101 성아 getId -> getNo 수정
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<>(orderHistDtos, pageable, totalCount);
    }

    // 현재 로그인한 사용자와 주문 생성자가 동일한지 검증
    @Override
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        return StringUtils.equals(curMember.getEmail(), savedMember.getEmail());
    }

    // 주문 취소
    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    // 주문 취소 요청
    @Override
    public void requestCancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.requestCancelOrder();
    }

    // 장바구니 주문
     // 241030 은열 수정
    @Override
    public Long orders(List<OrderDto> orderDtoList, String email) {

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();
        int totalPrice = 0; // 총 가격을 저장할 변수

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
            totalPrice += item.getPrice() * orderDto.getCount();
        }

        int discount = discountService.discount(member,totalPrice);
        Order order = Order.createOrder(member, orderItemList,discount, totalPrice - discount);
        order.setFinalPrice(totalPrice - discount); // 최종 가격 계산
        orderRepository.save(order);

        return order.getId();
    }

    // 관리자용 주문 이력 페이지
    @Override
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderListByAdmin(Pageable pageable) {
        List<Order> orders = orderRepository.findOrdersByAdmin(pageable);
        Long totalCount = orderRepository.countOrderByAdmin();

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemNoAndRepimgYn(orderItem.getItem().getNo(), "Y"); // 1101 성아 getId -> getNo 수정
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<>(orderHistDtos, pageable, totalCount);
    }
}
