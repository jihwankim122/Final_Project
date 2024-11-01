package com.top.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    //review num
    private Long reviewnum;

    //Item id
    private Long no; // 1101 성아 id -> no 수정

    //Membmer id
    private Long id; // 1101 성아 mid -> id 원복

    //Member name
    private String name;

    //Member email
    private String email;

    // 별점
    private int grade;

    // 내용
    private String text;

    private LocalDateTime regTime;

    private LocalDateTime updateTime;

}