package com.top.service;

import com.top.dto.NoticeDTO;
import com.top.dto.NpageRequestDTO;
import com.top.dto.NpageResultDTO;
import com.top.entity.Notice;
import org.springframework.data.domain.PageRequest;

public interface NoticeService {

    Long register(NoticeDTO dto); // Regist
    NpageResultDTO<NoticeDTO, Notice> getList(NpageRequestDTO requestDTO); // List
    NoticeDTO read(Long gno); // View Detail
    void modify(NoticeDTO dto);
    void remove(Long gno);

    // Changing DTO to ENTITY
    default Notice dtoToEntity(NoticeDTO dto){
        Notice entity = Notice.builder()
                .gno(dto.getGno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();
        return entity;
    }

    // Changing ENTITY to DTO
    default NoticeDTO entityToDto(Notice entity){
        NoticeDTO dto = NoticeDTO.builder()
                .gno(entity.getGno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .regDate(entity.getRegTime())
                .modDate(entity.getUpdateTime())
                .build();
        return dto;
    }

}
