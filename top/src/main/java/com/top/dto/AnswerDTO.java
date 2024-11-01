package com.top.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnswerDTO {
    private Long id;
    private String content;
    private String writer; // Writer Email
    private String writerName; // Writer Name
    private Long qid; // Qna ID
    private Long mid; // Member ID

    private LocalDateTime regTime;
    private LocalDateTime updateTime;
}
