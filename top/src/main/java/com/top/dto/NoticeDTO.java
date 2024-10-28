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
public class NoticeDTO {
    private Long nno;
    private String title;
    private String content;
    private String writer;
    private String writerName; // 25 Oct
    private Long memberId;  // 25 Oct
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
