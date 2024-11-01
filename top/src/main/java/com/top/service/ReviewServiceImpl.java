package com.top.service;

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


    @Override
    public List<ReviewDto> getListOfItem(Long itemId) { // 1101 성아 Long id -> Long itemId 수정
        Item item = new Item(); // 새로운 Item 객체 생성
        item.setNo(itemId); // 1101 성아 setId(id) - > setNo(itemId) 수정

        List<Review> result = reviewRepository.findByItem(item);
        return result.stream()
                .map(this::entityToDto) // entityToDto 메서드를 사용하여 DTO로 변환
                .collect(Collectors.toList());
    }

    @Override
    public Long register(ReviewDto itemReviewDto) {
        Review itemReview = dtoToEntity(itemReviewDto);
        reviewRepository.save(itemReview);
        return itemReview.getReviewnum();
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

