package com.top.service;

import java.util.List;
import com.top.dto.ReviewDto;
import com.top.entity.Item;
import com.top.entity.Member;
import com.top.entity.Review;

public interface ReviewService {    
    
    List<ReviewDto> getListOfItem(Long id); // 목록   
    Long register(ReviewDto itemReviewDto); // 등록    
    void modify(ReviewDto itemReviewDto); // 수정    
    void remove(Long reviewnum); // 삭제

    default Review dtoToEntity(ReviewDto itemReviewDto) {
        Review itemReview = Review.builder()
                .id(itemReviewDto.getReviewnum())
                .item(Item.builder().id(itemReviewDto.getId()).build())
                .member(Member.builder().id(itemReviewDto.getMember_id()).build())
                .grade(itemReviewDto.getGrade())
                .text(itemReviewDto.getText())
                .build();

        return itemReview;
    }

    default ReviewDto entityToDto(Review itemReview) {
        ReviewDto itemReviewDto = ReviewDto.builder()
                .reviewnum(itemReview.getId())
                .member_id(itemReview.getMember().getId())
                .id(itemReview.getItem().getId())              
                .email(itemReview.getMember().getEmail())
                .grade(itemReview.getGrade())
                .text(itemReview.getText())
                .regTime(itemReview.getRegTime())
                .updateTime(itemReview.getUpdateTime())
                .build();

        return itemReviewDto;
    }
}
