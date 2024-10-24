package com.top.controller;

import com.top.dto.CartDetailDto;
import com.top.dto.CartItemDto;
import com.top.dto.CartOrderDto;
import com.top.entity.Member;
import com.top.service.CartServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartServiceImpl cartService;
    private final HttpSession httpSession;

    @PostMapping(value = "/cart")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                         BindingResult bindingResult, Principal principal){

        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // 세션에서 회원 정보 추출 1024 유진 추가
        Member member = (Member) httpSession.getAttribute("member");
        if (member == null) {
            return new ResponseEntity<>("로그인된 회원이 없습니다.", HttpStatus.UNAUTHORIZED);
        }
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto,member.getEmail());
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @GetMapping(value = "/cart")
    public String orderHist(Model model) {
        // 세션에서 회원 정보 추출 1024 유진 추가
        Member member = (Member) httpSession.getAttribute("member");
        if (member == null) {
            return "redirect:/members/login";
        }

        List<CartDetailDto> cartDetailList = cartService.getCartList(member.getEmail());
        model.addAttribute("cartItems", cartDetailList);
        return "cart/cartList";
    }

    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity<?> updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                          int count) {
        if (count <= 0) {
            return new ResponseEntity<>("1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        }

        // 세션에서 회원 정보 추출 1024 유진 추가
        Member member = (Member) httpSession.getAttribute("member");
        if (member == null) {
            return new ResponseEntity<>("로그인된 회원이 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        if (!cartService.validateCartItem(cartItemId, member.getEmail())) {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity<?> deleteCartItem(@PathVariable("cartItemId") Long cartItemId) {
        // 세션에서 회원 정보 추출 1024 유진 추가
        Member member = (Member) httpSession.getAttribute("member");
        if (member == null) {
            return new ResponseEntity<>("로그인된 회원이 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        if (!cartService.validateCartItem(cartItemId, member.getEmail())) {
            return new ResponseEntity<>("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity<?> orderCartItem(@RequestBody CartOrderDto cartOrderDto) {
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if (cartOrderDtoList == null || cartOrderDtoList.isEmpty()) {
            return new ResponseEntity<>("상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        // 세션에서 회원 정보 추출 1024 유진 추가
        Member member = (Member) httpSession.getAttribute("member");
        if (member == null) {
            return new ResponseEntity<>("로그인된 회원이 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if (!cartService.validateCartItem(cartOrder.getCartItemId(), member.getEmail())) {
                return new ResponseEntity<>("권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList, member.getEmail());
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }
}
