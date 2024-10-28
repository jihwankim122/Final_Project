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
    private Long qid; // Qna ID
    private Long mid; //

    private LocalDateTime regTime;
    private LocalDateTime updateTime;
}
