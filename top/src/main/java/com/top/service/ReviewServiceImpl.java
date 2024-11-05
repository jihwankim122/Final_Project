package com.top.service;

import com.top.entity.Member;
import com.top.repository.ItemRepository;
import com.top.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import com.top.dto.ReviewDto;
import com.top.entity.Item;
import com.top.entity.Review;
import com.top.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository; // 1105 유진 추가
    private final ItemRepository itemRepository; // 1105 유진 추가


    @Override
    public List<ReviewDto> getListOfItem(Long itemId) { // 1101 성아 Long id -> Long itemId 수정
        Item item = new Item(); // 새로운 Item 객체 생성
        item.setNo(itemId); // 1101 성아 setId(id) - > setNo(itemId) 수정

        List<Review> result = reviewRepository.findByItem(item);
        return result.stream()
                .map(this::entityToDto) // entityToDto 메서드를 사용하여 DTO로 변환
                .collect(Collectors.toList());
    }

    // 1105 유진 수정
    @Override
    public Long register(ReviewDto itemReviewDto) {
        // member와 item을 조회하여 Review 객체에 설정
        Member member = memberRepository.findById(itemReviewDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원 ID입니다."));
        Item item = itemRepository.findById(itemReviewDto.getNo())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품 ID입니다."));

        Review itemReview = dtoToEntity(itemReviewDto, member, item);
        reviewRepository.save(itemReview);
        return itemReview.getReviewnum();
    }

    private Review dtoToEntity(ReviewDto dto, Member member, Item item) {
        return Review.builder()
                .member(member)
                .item(item)
                .grade(dto.getGrade())
                .text(dto.getText())
                .build();
    }

    @Override
    public void modify(ReviewDto itemReviewDto) {
        Optional<Review> result = reviewRepository.findById(itemReviewDto.getReviewnum());
        if(result.isPresent()){
            Review itemReview = result.get();
            itemReview.changeGrade(itemReviewDto.getGrade());
            itemReview.changeText(itemReviewDto.getText());
            reviewRepository.save(itemReview);
        }

    }

    @Override
    public void remove(Long reviewnum) {
        reviewRepository.deleteById(reviewnum);
    }
}

