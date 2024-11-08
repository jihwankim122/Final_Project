package com.top.service;

import com.top.dto.CartItemDto;
import com.top.dto.WishlistItemDto;
import com.top.dto.WishlistDetailDto;
import com.top.entity.Wishlist;
import com.top.entity.WishlistItem;
import com.top.entity.Item;
import com.top.entity.Member;
import com.top.repository.WishlistItemRepository;
import com.top.repository.WishlistRepository;
import com.top.repository.ItemRepository;
import com.top.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final CartServiceImpl cartService;

    @Override
    public Long addWishlist(WishlistItemDto wishlistItemDto, String email) {
        // 상품 조회
        Item item = itemRepository.findById(wishlistItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        // 회원 조회
        Member member = memberRepository.findByEmail(email);

        // 사용자의 위시리스트 조회
        Wishlist wishlist = wishlistRepository.findByMemberId(member.getId());
        if (wishlist == null) {
            wishlist = Wishlist.createWishlist(member);
            wishlistRepository.save(wishlist);
        }

        // 해당 상품이 이미 찜한 목록에 있는지 확인
        WishlistItem savedWishlistItem = wishlistItemRepository.findByWishlistIdAndItemNo(wishlist.getId(), item.getNo());
        if (savedWishlistItem != null) {
            // 이미 찜한 상품이라면 해당 상품 ID를 반환
            return savedWishlistItem.getId();
        } else {
            // 찜 목록에 없으면 새로 추가
            WishlistItem wishlistItem = WishlistItem.createWishlistItem(wishlist, item);
            wishlistItemRepository.save(wishlistItem);
            return wishlistItem.getId();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistDetailDto> getWishlist(String email) {
        List<WishlistDetailDto> wishlistDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Wishlist wishlist = wishlistRepository.findByMemberId(member.getId());
        if (wishlist == null) {
            return wishlistDetailDtoList;
        }


        wishlistDetailDtoList = wishlistItemRepository.findWishlistDetailDtoList(wishlist.getId());
        return wishlistDetailDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateWishlistItem(Long wishlistItemId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = wishlistItem.getWishlist().getMember();

        return curMember.getEmail().equals(savedMember.getEmail());
    }

    @Override
    public void deleteWishlistItem(Long wishlistItemId) {
        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(EntityNotFoundException::new);
        wishlistItemRepository.delete(wishlistItem);
    }

    @Override
    public void deleteWishlistItemByItemId(Long itemId) {
        List<WishlistItem> wishlistItems = wishlistItemRepository.findByItemNo(itemId);
        for (WishlistItem wishlistItem : wishlistItems) {
            wishlistItemRepository.delete(wishlistItem);
        }
    }

    // 06Nov 의빈추가
    @Transactional
    public Long moveItemToCart(Long wishlistItemId, String email) {
        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(EntityNotFoundException::new);
        Item item = wishlistItem.getItem();
        Member member = memberRepository.findByEmail(email);

        // add to cart
        CartItemDto cartItemDto = new CartItemDto(item.getNo(), 1); // Setting default value 1
        return cartService.addCart(cartItemDto, email);
    }
    // 08Nov 의빈추가
    @Override
    @Transactional(readOnly = true)
    public boolean isItemAlreadyWished(Long itemId, String email) {
        Member member = memberRepository.findByEmail(email);
        Wishlist wishlist = wishlistRepository.findByMemberId(member.getId());

        if (wishlist == null) {
            return false;  // 위시리스트가 없다면, 당연히 중복 찜할 수 없습니다.
        }

        // 해당 상품이 이미 찜 목록에 있는지 확인
        WishlistItem savedWishlistItem = wishlistItemRepository.findByWishlistIdAndItemNo(wishlist.getId(), itemId);
        return savedWishlistItem != null;
    }
}
