package com.top.service;


import com.top.dto.NpageRequestDTO;
import com.top.dto.NpageResultDTO;
import com.top.dto.QnaDTO;
import com.top.entity.Member;
import com.top.entity.Qna;

public interface QnaService {

    Long register(QnaDTO dto); // Register
    NpageResultDTO<QnaDTO, Qna> getList(NpageRequestDTO requestDTO); // List
    QnaDTO read(Long qno); // View Detail
    void modify(QnaDTO dto);
    void remove(Long qno);
    Qna getQnaById(Long qno); // Qna Searching


    // Changing DTO to ENTITY
    default Qna dtoToEntity(QnaDTO dto, Member member) {
        return Qna.builder()
                .qno(dto.getQno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(member.getEmail()) // Writer Email
                .member(member) // Connecting Member
                .build();
    }

    // Changing ENTITY to DTO
    default QnaDTO entityToDto(Qna entity) {
        return QnaDTO.builder()
                .qno(entity.getQno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .writerName(entity.getMember().getName()) // Getting Name from Member
                .regDate(entity.getRegTime())
                .modDate(entity.getUpdateTime())
                .build();
    }
}
