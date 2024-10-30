package com.top.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.top.entity.Member;
import com.top.repository.MemberRepository;
import org.springframework.stereotype.Service;
import com.top.dto.ReviewDto;
import com.top.entity.Item;
import com.top.entity.Review;
import com.top.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository; // 주입
    private final MemberRepository memberRepository;

    @Override
    public List<ReviewDto> getListOfItem(Long id) {
        // Item 객체 생성 및 설정
        Item item = Item.builder().id(id).build();

        List<Review> result = reviewRepository.findByItem(item);

        // Review 객체를 ReviewDto로 변환하여 리스트 반환
        return result.stream().map(itemReview -> entityToDto(itemReview)).collect(Collectors.toList());
    }

    @Override
    public Long register(ReviewDto itemReviewDto) {
        // 로그인한 사용자 이메일 가져오기
        String email = getCurrentUserEmail();
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("로그인한 사용자가 없습니다.");
        }

        // Member 찾기
        Member member = memberRepository.findByEmail(email);

        // ReviewDto를 Review 엔티티로 변환
        Review itemReview = dtoToEntity(itemReviewDto);
        itemReview.setMember(member);

        // Review 저장
        reviewRepository.save(itemReview);

        return itemReview.getId();
    }

    @Override
    public void modify(ReviewDto itemReviewDto) {
        Optional<Review> result = reviewRepository.findById(itemReviewDto.getReviewnum());

        if (result.isPresent()) {
            Review itemReview = result.get();
            // 필드 값 수정
            itemReview.changeGrade(itemReviewDto.getGrade());
            itemReview.changeText(itemReviewDto.getText());

            // 수정된 Review 저장
            reviewRepository.save(itemReview);
        }
    }

    @Override
    public void remove(Long reviewnum) {
        // Review 삭제
        reviewRepository.deleteById(reviewnum);
    }

    // 현재 로그인한 사용자의 이메일 가져오기
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername(); // 이메일 반환
            } else if (principal instanceof String) {
                return (String) principal; // principal이 문자열일 경우
            }
        }
        return null; // 인증되지 않은 사용자일 경우
    }
}
