package com.top.service;


import com.top.dto.NoticeDTO;
import com.top.dto.NpageRequestDTO;
import com.top.dto.NpageResultDTO;
import com.top.dto.QnaDTO;
import com.top.entity.Member;
import com.top.entity.Notice;
import com.top.entity.Qna;

public interface QnaService {

    Long register(QnaDTO dto); // Regist
    NpageResultDTO<QnaDTO, Qna> getList(NpageRequestDTO requestDTO); // List
    QnaDTO read(Long qno); // View Detail
    void modify(QnaDTO dto);
    void remove(Long qno);

    // Changing DTO to ENTITY
    default Qna dtoToEntity(QnaDTO dto){
        Qna entity = Qna.builder()
                .qno(dto.getQno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();
        return entity;
    }

    // Changing ENTITY to DTO
    default QnaDTO entityToDto(Qna entity){
        QnaDTO dto = QnaDTO.builder()
                .qno(entity.getQno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .regDate(entity.getRegTime())
                .modDate(entity.getUpdateTime())
                .build();
        return dto;
    }

    // Creating ENTITY  28 Oct
    default Qna createQnaEntity(QnaDTO dto, Member member) {
        return Qna.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(member.getEmail()) // Writer Email
                .member(member) // Connecting Member
                .build();
    }

}
