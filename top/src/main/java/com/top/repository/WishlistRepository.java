package com.top.repository;

import com.top.entity.Wishlist;
import com.top.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Wishlist findByMemberId(Long memberId);

}
