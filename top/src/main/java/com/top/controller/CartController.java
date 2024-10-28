package com.top.controller;

import com.top.dto.CartDetailDto;
import com.top.dto.CartItemDto;
import com.top.dto.CartOrderDto;
import com.top.entity.Member;
import com.top.security.dto.ClubAuthMemberDto;
import com.top.service.CartServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartServiceImpl cartService;

    // 현재 로그인된 사용자 정보를 가져오는 메서드
    private Member getLoggedInMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        String email;

        if (principal instanceof User) {
            // 일반 로그인 사용자인 경우
            email = ((User) principal).getUsername();
        } else if (principal instanceof ClubAuthMemberDto) {
            // 소셜 로그인 사용자인 경우
            email = ((ClubAuthMemberDto) principal).getEmail();
        } else {
            throw new IllegalStateException("로그인된 사용자가 없습니다.");
        }

        // 이메일로 회원 조회
        return cartService.findMemberByEmail(email);
    }

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity<?> order(@RequestBody @Valid CartItemDto cartItemDto,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                sb.append(error.getDefaultMessage());
            }
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            Member member = getLoggedInMember();  // 로그인된 회원 정보 조회
            Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());
            return new ResponseEntity<>(cartItemId, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/cart")
    public String orderHist(Model model) {
        try {
            Member member = getLoggedInMember();  // 로그인된 회원 정보 조회
            List<CartDetailDto> cartDetailList = cartService.getCartList(member.getEmail());
            model.addAttribute("cartItems", cartDetailList);
            return "cart/cartList";
        } catch (IllegalStateException e) {
            return "redirect:/members/login";  // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }
    }

    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity<?> updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                          int count) {
        if (count <= 0) {
            return new ResponseEntity<>("1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        }

        try {
            Member member = getLoggedInMember();  // 로그인된 회원 정보 조회

            if (!cartService.validateCartItem(cartItemId, member.getEmail())) {
                return new ResponseEntity<>("권한이 없습니다.", HttpStatus.FORBIDDEN);
            }

            cartService.updateCartItemCount(cartItemId, count);
            return new ResponseEntity<>(cartItemId, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>("로그인된 회원이 없습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity<?> deleteCartItem(@PathVariable("cartItemId") Long cartItemId) {
        try {
            Member member = getLoggedInMember();  // 로그인된 회원 정보 조회

            if (!cartService.validateCartItem(cartItemId, member.getEmail())) {
                return new ResponseEntity<>("권한이 없습니다.", HttpStatus.FORBIDDEN);
            }

            cartService.deleteCartItem(cartItemId);
            return new ResponseEntity<>(cartItemId, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>("로그인된 회원이 없습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity<?> orderCartItem(@RequestBody CartOrderDto cartOrderDto) {
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if (cartOrderDtoList == null || cartOrderDtoList.isEmpty()) {
            return new ResponseEntity<>("상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        try {
            Member member = getLoggedInMember();  // 로그인된 회원 정보 조회

            for (CartOrderDto cartOrder : cartOrderDtoList) {
                if (!cartService.validateCartItem(cartOrder.getCartItemId(), member.getEmail())) {
                    return new ResponseEntity<>("권한이 없습니다.", HttpStatus.FORBIDDEN);
                }
            }

            Long orderId = cartService.orderCartItem(cartOrderDtoList, member.getEmail());
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>("로그인된 회원이 없습니다.", HttpStatus.UNAUTHORIZED);
        }
    }
}
