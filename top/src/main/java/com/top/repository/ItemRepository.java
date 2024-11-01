package com.top.repository;

import com.top.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long>,  // entity , ID key type 넣기
    QuerydslPredicateExecutor<Item>, ItemRepositoryCustom{

        List<Item> findByItemNm (String itemNum);

        List<Item> findByItemNmOrItemDetail (String itemNm, String itemDetail);

        List<Item> findByPriceLessThan (Integer price);

        List<Item> findByPriceLessThanOrderByPriceDesc (Integer price);

        @Query("select i from Item i where i.itemDetail like " +
                "%:itemDetail% order by i.price desc")
        List<Item> findByItemDetail (@Param("itemDetail") String itemDetail);

        @Query(value = "select * from item i where i.item_detail like " +
                "%:itemDetail% order by i.price desc", nativeQuery = true)
        List<Item> findByItemDetailByNative (@Param("itemDetail") String itemDetail);

        // 1028 성아 추가 (별점 평균) // 1101 성아 item.id -> item.no 변경
        @Query("SELECT AVG(r.grade) FROM Review r WHERE r.item.no = :itemId")
        Double getAverageRating(@Param("itemId") Long itemId);

        // 1028 성아 추가 (리뷰 개수) // 1101 성아 item.id -> item.no 변경
        @Query("SELECT COUNT(r) FROM Review r WHERE r.item.no = :itemId")
        Integer getReviewCount(@Param("itemId") Long itemId);

    }

