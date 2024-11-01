package com.top.service;

import com.top.dto.CartItemDto;
import com.top.entity.Cart;
import com.top.entity.CartItem;
import com.top.entity.Item;
import com.top.entity.Member;
import com.top.repository.CartItemRepository;
import com.top.repository.CartRepository;
import com.top.repository.ItemRepository;
import com.top.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import com.top.dto.CartDetailDto;
import com.top.dto.CartOrderDto;
import com.top.dto.OrderDto;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderServiceImpl orderService;

     @Autowired
    private DiscountService discountService; //241028 은열 추가

    // 은열 241031수정
    @Override
    public Long addCart(CartItemDto cartItemDto, String email) {
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemNo(cart.getId(), item.getNo()); // 1101 성아 getId -> getNo 수정

        if (savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());
            updatePrices(savedCartItem);
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount(), member);
            updatePrices(cartItem);
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }
   // 은열 241031추가
    private void updatePrices(CartItem cartItem) {
        int itemPrice = cartItem.getItem().getPrice();
        int totalPrice = itemPrice * cartItem.getCount();
        int discountAmount = discountService.discount(cartItem.getMember(), totalPrice);

        cartItem.calculatePrices(discountAmount, totalPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();

        return StringUtils.equals(curMember.getEmail(), savedMember.getEmail());
    }

    @Override
    public void updateCartItemCount(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    @Override
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {
        List<OrderDto> orderDtoList = new ArrayList<>();

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getNo()); // 1101 성아 getId -> getNo 수정
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }

    @Override
    public void deleteCartItemByItemId(Long itemId) {
        // 특정 아이템 ID에 해당하는 모든 장바구니 항목을 조회
        List<CartItem> cartItems = cartItemRepository.findByItemNo(itemId); // 1101 성아 findByItemId -> findByItemNo 수정

        // 각 장바구니 항목 삭제
        for (CartItem cartItem : cartItems) {
            cartItemRepository.delete(cartItem);
        }
    }

    public Member findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalStateException("해당 이메일의 회원을 찾을 수 없습니다: " + email);
        }
        return member;
    }
}
