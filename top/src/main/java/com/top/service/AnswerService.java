package com.top.service;

import com.top.dto.AnswerDTO;
import com.top.entity.Answer;
import com.top.entity.Qna;
import java.util.List;

public interface AnswerService {

    Long register(AnswerDTO dto); // Regist
    List<AnswerDTO> getList(Qna qna);
    void modify(AnswerDTO dto); // Update
    void remove(Long id); // Delete

    // Changing DTO to ENTITY
    default Answer dtoToEntity(AnswerDTO dto, Qna qna) {
        return Answer.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .qna(qna)
                .build();
    }

    // Changing ENTITY to DTO
    default AnswerDTO entityToDto(Answer entity) {
        return AnswerDTO.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .qid(entity.getQna() != null ? entity.getQna().getQno() : null) // Qna ID
                .mid(entity.getMember() != null ? entity.getMember().getId() : null) // Member ID
                .regTime(entity.getRegTime()) // Date
                .updateTime(entity.getUpdateTime()) // Modify Date
                .build();
    }
}
