package com.top.controller;

import com.top.dto.OrderDto;
import com.top.dto.OrderHistDto;
import com.top.security.dto.ClubAuthMemberDto;
import com.top.service.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    // 로그인 상태 확인
    @GetMapping(value = "/order")
    public @ResponseBody ResponseEntity<Boolean> checkLoginStatus(Principal principal) {
        boolean isLoggedIn = principal != null;
        return new ResponseEntity<>(isLoggedIn, HttpStatus.OK);
    }

    // 주문하기
    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto,
                                                 BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            String email = getLoggedInUserEmail(); // 로그인한 사용자 이메일 가져오기
            Long orderId = orderService.order(orderDto, email);
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 주문 이력 조회
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Model model) {
        try {
            String email = getLoggedInUserEmail(); // 로그인한 사용자 이메일 가져오기
            Pageable pageable = PageRequest.of(page.orElse(0), 4);
            Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(email, pageable);

            model.addAttribute("orders", ordersHistDtoList);
            model.addAttribute("page", pageable.getPageNumber());
            model.addAttribute("maxPage", 5);

            return "order/orderHist";
        } catch (IllegalStateException e) {
            return "redirect:/members/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
    }

    // 관리자용 주문 이력 조회
    @GetMapping(value = {"admin/orders", "admin/orders/{page}"})
    public String orderHistByAdmin(@PathVariable("page") Optional<Integer> page, Model model) {
        Pageable pageable = PageRequest.of(page.orElse(0), 10);
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderListByAdmin(pageable);

        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/allOrders";
    }

    // 주문 취소 승인 (관리자)
    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity<Long> cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    // 주문 취소 요청 (일반 유저)
    @PostMapping("/order/{orderId}/requestCancel")
    public @ResponseBody ResponseEntity<?> requestCancelOrder(@PathVariable("orderId") Long orderId) {
        try {
            String email = getLoggedInUserEmail(); // 로그인한 사용자 이메일 가져오기
            if (!orderService.validateOrder(orderId, email)) {
                return new ResponseEntity<>("취소 요청 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }

            orderService.requestCancelOrder(orderId);
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>("로그인된 회원이 없습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    // 로그인한 사용자의 이메일 가져오기 (소셜/일반 로그인 처리)
    private String getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("로그인된 사용자가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getUsername(); // 일반 로그인 사용자의 이메일 반환
        } else if (principal instanceof ClubAuthMemberDto) {
            return ((ClubAuthMemberDto) principal).getEmail(); // 소셜 로그인 사용자의 이메일 반환
        } else {
            throw new IllegalStateException("알 수 없는 사용자 타입입니다.");
        }
    }
}
