package com.top.controller;

import com.top.dto.ReviewDto;
import com.top.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Log4j2
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 목록
    @GetMapping("/{itemId}/all")
    public ResponseEntity<List<ReviewDto>> getList(@PathVariable("itemId") Long itemId) {
        log.info("--------------list---------------");
        log.info("Item id: " + itemId);

        List<ReviewDto> reviewDTOList = reviewService.getListOfItem(itemId);

        return new ResponseEntity<>(reviewDTOList, HttpStatus.OK);
    }

    // 등록
    @PreAuthorize("isAuthenticated()") // 로그인을 했을 때만 접근 가능
    @PostMapping("/{itemId}")
    public ResponseEntity<Long> addReview(@RequestBody ReviewDto itemReviewDto){
        log.info("--------------add ItemReview---------------");
        log.info("reviewDTO: " + itemReviewDto);

        Long reviewnum = reviewService.register(itemReviewDto);

        return new ResponseEntity<>( reviewnum, HttpStatus.OK);
    }

    // 수정
    // @PreAuthorize("principal.username == #movieReviewDto.mid")
    @PutMapping("/{itemId}/{reviewnum}")
    public ResponseEntity<Long> modifyReview(@PathVariable Long reviewnum,
                                             @RequestBody ReviewDto itemReviewDto){
        log.info("---------------modify ItemReview--------------" + reviewnum);
        log.info("reviewDTO: " + itemReviewDto);

        reviewService.modify(itemReviewDto);

        return new ResponseEntity<>( reviewnum, HttpStatus.OK);
    }

    // 삭제
    @DeleteMapping("/{itemId}/{reviewnum}")
    public ResponseEntity<Long> removeReview( @PathVariable Long reviewnum){
        log.info("---------------modify removeReview--------------");
        log.info("reviewnum: " + reviewnum);

        reviewService.remove(reviewnum);

        return new ResponseEntity<>( reviewnum, HttpStatus.OK);
    }

}

