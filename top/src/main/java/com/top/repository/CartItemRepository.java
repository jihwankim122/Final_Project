package com.top.repository;

import com.top.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import com.top.dto.CartDetailDto;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndItemNo(Long cartId, Long itemId); // 1101 성아 수정

    // 1101 성아 item.id -> item.no 변경
    @Query("select new com.top.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.no = ci.item.no " +
            "and im.repimgYn = 'Y' " +
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);

    // 특정 아이템 ID에 해당하는 모든 장바구니 항목 조회
    List<CartItem> findByItemNo(Long itemId); // 1101 성아 수정

    // 장바구니에 담긴 특정 상품을 삭제하는 메서드
    void deleteByItemNo(Long itemId); // 1101 성아 수정

}