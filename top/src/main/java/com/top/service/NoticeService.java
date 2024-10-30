package com.top.service;

import com.top.dto.NoticeDTO;
import com.top.dto.NpageRequestDTO;
import com.top.dto.NpageResultDTO;
import com.top.entity.Member;
import com.top.entity.Notice;

public interface NoticeService {

    Long register(NoticeDTO dto); // Regist
    NpageResultDTO<NoticeDTO, Notice> getList(NpageRequestDTO requestDTO); // List
    NoticeDTO read(Long nno); // View Detail
    void modify(NoticeDTO dto);
    void remove(Long nno);

    // Changing DTO to ENTITY
    default Notice dtoToEntity(NoticeDTO dto){
        Notice entity = Notice.builder()
                .nno(dto.getNno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();
        return entity;
    }

    // Changing ENTITY to DTO
    default NoticeDTO entityToDto(Notice entity){
        NoticeDTO dto = NoticeDTO.builder()
                .nno(entity.getNno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .regDate(entity.getRegTime())
                .modDate(entity.getUpdateTime())
                .build();
        return dto;
    }

    // Creating ENTITY
    default Notice createNoticeEntity(NoticeDTO dto, Member member) {
        return Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(member.getEmail()) // Writer Email
                .member(member) // Connecting Member
                .build();
    }

}
