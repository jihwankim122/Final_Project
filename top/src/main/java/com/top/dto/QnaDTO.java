package com.top.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QnaDTO {
    private Long qno;
    private String title;
    private String content;
    private String writer;
    private String writerName; // 28 Oct
    private Long memberId;  // 28 Oct
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private boolean hasAnswer; // 01 Nov


}
