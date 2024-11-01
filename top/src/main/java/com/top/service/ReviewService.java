package com.top.service;

import com.top.dto.ReviewDto;
import com.top.entity.Member;
import com.top.entity.Item;
import com.top.entity.Review;

import java.util.List;

public interface ReviewService {

    //상품의 모든 리뷰를 가져온다.
    List<ReviewDto> getListOfItem(Long itemId); // 1101 성아 Long id -> Long itemId 변경

    //상품 리뷰를 추가
    Long register(ReviewDto itemReviewDTo);

    //특정한 상품 리뷰 수정
    void modify(ReviewDto itemReviewDto);

    //상품 리뷰 삭제
    void remove(Long reviewnum);

    default Review dtoToEntity(ReviewDto itemReviewDto) {
        Review itemReview = new Review();

        itemReview.setReviewnum(itemReviewDto.getReviewnum());

        // Review 엔티티의 item 필드를 Item 객체로 설정
        Item item = new Item();
        item.setNo(itemReviewDto.getNo()); // 1101 성아 item.setId(itemReviewDto.getId()); -> item.setNo(itemReviewDto.getNo()); 수정
        itemReview.setItem(item);

        // Review 엔티티의 member 필드를 Member 객체로 설정
        Member member = new Member();
        member.setId(itemReviewDto.getId()); // 1101 성아 member.setMid(itemReviewDto.getMid()); -> member.setId(itemReviewDto.getId()); 수정
        itemReview.setMember(member);

        itemReview.setGrade(itemReviewDto.getGrade());
        itemReview.setText(itemReviewDto.getText());

        return itemReview;
    }

    default ReviewDto entityToDto(Review itemReview){
        ReviewDto itemReviewDto = ReviewDto.builder()
                .reviewnum(itemReview.getReviewnum())
                .no(itemReview.getItem().getNo()) // 1101 성아 .id(itemReview.getItem().getId()) -> .no(itemReview.getItem().getNo()) 수정
                .id(itemReview.getMember().getId()) // 1101 성아 .mid(itemReview.getMember().getMid()) -> .id(itemReview.getMember().getId()) 수정
                .name(itemReview.getMember().getName())
                .email(itemReview.getMember().getEmail())
                .grade(itemReview.getGrade())
                .text(itemReview.getText())
                .regTime(itemReview.getRegTime())
                .updateTime(itemReview.getUpdateTime())
                .build();
        return itemReviewDto;
    }
}
