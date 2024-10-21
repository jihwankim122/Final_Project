package com.top.service;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.top.dto.NoticeDTO;
import com.top.dto.NpageRequestDTO;
import com.top.dto.NpageResultDTO;
import com.top.entity.Notice;
import com.top.entity.QNotice;
import com.top.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;


@Service
@Log4j2
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository repository;

    // Regist
    @Override
    public Long register(NoticeDTO dto) {
        log.info("DTO------------------------");
        log.info(dto);
        Notice entity = dtoToEntity(dto); // DTOtoENTITY
        log.info(entity);
        repository.save(entity); // Regist
        return entity.getGno(); // List Number
    }

    @Override
    public NpageResultDTO<NoticeDTO, Notice> getList(NpageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending()); // order by gno desc limit 0,10
        BooleanBuilder booleanBuilder = getSearch(requestDTO);

        Page<Notice> result = repository.findAll(booleanBuilder, pageable); // Using Querydsl
        Function<Notice, NoticeDTO> fn = (entity -> entityToDto(entity)); // Create fn Reference
        return new NpageResultDTO<>(result, fn);
    }


    private BooleanBuilder getSearch(NpageRequestDTO requestDTO) {
        String type = requestDTO.getType(); // 검색필드
        BooleanBuilder booleanBuilder = new BooleanBuilder(); // where
        QNotice qnotice = QNotice.notice;
        String keyword = requestDTO.getKeyword(); // 검색어
        BooleanExpression expression = qnotice.gno.gt(0L); // gno > 0 조건만 생성
        booleanBuilder.and(expression); // where gno > 0
        if (type == null || type.trim().length() == 0) { //검색 조건이 없는 경우
            return booleanBuilder;
        }


        BooleanBuilder conditionBuilder = new BooleanBuilder();
        if (type.contains("t")) {
            conditionBuilder.or(qnotice.title.contains(keyword)); // or title like '%검색어%'
        }
        if (type.contains("c")) {
            conditionBuilder.or(qnotice.content.contains(keyword)); // or content like '%검색어%'
        }
        if (type.contains("w")) {
            conditionBuilder.or(qnotice.writer.contains(keyword)); // or writer like '%검색어%'
        }

        booleanBuilder.and(conditionBuilder);
        return booleanBuilder;
    }

    // Read
    @Override
    public NoticeDTO read(Long gno) {
        Optional<Notice> result = repository.findById(gno);
        return result.isPresent() ? entityToDto(result.get()) : null;
    }

    // Update
    @Override
    public void modify(NoticeDTO dto) {
        //업데이트 하는 항목은 '제목', '내용'
        Optional<Notice> result = repository.findById(dto.getGno());
        if (result.isPresent()) {
            Notice entity = result.get();
            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());
            repository.save(entity);
        }
    }
    // Delete
    @Override
    public void remove(Long gno) {
        repository.deleteById(gno);
    }

}

