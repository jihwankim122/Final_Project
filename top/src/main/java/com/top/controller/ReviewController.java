package com.top.controller;

import java.util.List;

import com.top.dto.ItemFormDto;
import com.top.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.top.dto.ReviewDto;
import com.top.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/reviews")
@Log4j2
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ItemService itemService;
    // 목록 조회
    @GetMapping("/{id}/all")
    public ResponseEntity<List<ReviewDto>> getList(@PathVariable("id") Long id) {

        List<ReviewDto> reviewDTOList = reviewService.getListOfItem(id);
        return new ResponseEntity<>(reviewDTOList, HttpStatus.OK);
    }

    // 등록
    @PostMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> addReview(@PathVariable Long id, @RequestBody ReviewDto itemReviewDto) {

        Long reviewnum = reviewService.register(itemReviewDto);
        return new ResponseEntity<>(reviewnum, HttpStatus.OK);
    }

    // 수정
    @PutMapping("/{id}/{reviewnum}")
    @PreAuthorize("principal.username == #itemReviewDto.mid")
    public ResponseEntity<Long> modifyReview(@PathVariable Long reviewnum, @RequestBody ReviewDto itemReviewDto) {

        reviewService.modify(itemReviewDto);
        return new ResponseEntity<>(reviewnum, HttpStatus.OK);
    }

    // 삭제
    @DeleteMapping("/{id}/{reviewnum}")
    public ResponseEntity<Long> removeReview(@PathVariable Long reviewnum) {

        reviewService.remove(reviewnum);
        return new ResponseEntity<>(reviewnum, HttpStatus.OK);
    }

    // 현재 로그인한 사용자의 이메일 가져오기
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null; // 인증되지 않은 사용자일 경우
    }

    @GetMapping("/review")
    public String getReviewPage(@RequestParam(value = "itemId", required = false) Long itemId, Model model) {
        String email = getCurrentUserEmail();

        if (itemId != null) {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("item", itemFormDto);
        }

        model.addAttribute("itemId", itemId);
        model.addAttribute("email", email);

        // 템플릿 경로 명확히 지정
        return "item/review";
    }


}
