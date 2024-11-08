package com.top.repository;

import com.top.dto.WishlistDetailDto;
import com.top.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    WishlistItem findByIdAndItemNo(Long id, Long itemId);

    @Query("select new com.top.dto.WishlistDetailDto(wi.id, i.itemNm, i.price, im.imgUrl) " +
            "from WishlistItem wi " +
            "join wi.item i " +
            "join ItemImg im on im.item.id = i.id " +
            "where wi.wishlist.id = :wishlistId " +
            "and im.repimgYn = 'Y' " +
            "order by wi.regTime desc")
    List<WishlistDetailDto> findWishlistDetailDtoList(Long wishlistId);

    List<WishlistItem> findByItemNo(Long itemNo);

    WishlistItem findByWishlistIdAndItemNo(Long wishlistId, Long itemNo);



    void deleteByitemNo(Long itemId);

    }