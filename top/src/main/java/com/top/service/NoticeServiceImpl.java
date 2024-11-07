package com.top.service;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.top.dto.NoticeDTO;
import com.top.dto.NpageRequestDTO;
import com.top.dto.NpageResultDTO;
import com.top.entity.Member;
import com.top.entity.Notice;
import com.top.entity.QNotice;
import com.top.repository.MemberRepository;
import com.top.repository.NoticeRepository;

import jakarta.transaction.Transactional;
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
@Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository repository;
    private final MemberRepository memberRepository; // 25 Oct

    // Regist
    @Override
    public Long register(NoticeDTO dto) {
        log.info("DTO------------------------");
        log.info(dto);

        Member member = memberRepository.findByEmail(dto.getWriter());
        if (member == null) {
            throw new RuntimeException("Member not found");
        }

        // Call Creating Entitiy
        Notice entity = dtoToEntity(dto, member);

        log.info(entity);
        repository.save(entity); // Regist
        return entity.getNno(); // List Number
    }


    @Override
    public NpageResultDTO<NoticeDTO, Notice> getList(NpageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("nno").descending()); // order by nno desc limit 0,10
        BooleanBuilder booleanBuilder = getSearch(requestDTO);

        Page<Notice> result = repository.findAll(booleanBuilder, pageable); // Using Querydsl
        // 25 Oct ------------------------------------------------------------------------------------
        Function<Notice, NoticeDTO> fn = (entity) -> {
            NoticeDTO dto = entityToDto(entity); // Convert entity to DTO
            Member member = memberRepository.findByEmail(dto.getWriter()); // Member From Email
            if (member != null) {
                dto.setWriterName(member.getName()); // 25 Oct
            }
            return dto;
        };

        return new NpageResultDTO<>(result, fn);
    }




//    private BooleanBuilder getSearch(NpageRequestDTO requestDTO) {
//        String type = requestDTO.getType(); // 검색필드
//        BooleanBuilder booleanBuilder = new BooleanBuilder(); // where
//        QNotice qnotice = QNotice.notice;
//        String keyword = requestDTO.getKeyword(); // 검색어
//        BooleanExpression expression = qnotice.nno.gt(0L); // nno > 0 조건만 생성
//        booleanBuilder.and(expression); // where nno > 0
//        if (type == null || type.trim().length() == 0) { //검색 조건이 없는 경우
//            return booleanBuilder;
//        }
//
//
//        BooleanBuilder conditionBuilder = new BooleanBuilder();
//        if (type.contains("t")) {
//            conditionBuilder.or(qnotice.title.contains(keyword)); // or title like '%검색어%'
//        }
//        if (type.contains("c")) {
//            conditionBuilder.or(qnotice.content.contains(keyword)); // or content like '%검색어%'
//        }
//        if (type.contains("w")) {
//            conditionBuilder.or(qnotice.writer.contains(keyword)); // or writer like '%검색어%'
//        }
//
//        booleanBuilder.and(conditionBuilder);
//        return booleanBuilder;
//    }

    // Read
    @Override
    public NoticeDTO read(Long nno) {
        Optional<Notice> result = repository.findById(nno);
        return result.isPresent() ? entityToDto(result.get()) : null;
    }

    // Update
    @Override
    public void modify(NoticeDTO dto) {
        //업데이트 하는 항목은 '제목', '내용'
        Optional<Notice> result = repository.findById(dto.getNno());
        if (result.isPresent()) {
            Notice entity = result.get();
            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());
            repository.save(entity);
        }
    }
    // Delete
    @Override
    public void remove(Long nno) {
        repository.deleteById(nno);
    }

    // 고정 상태 변경
    @Override
    public void togglePinned(Long nno) {
        Optional<Notice> result = repository.findById(nno);
        if (result.isPresent()) {
            Notice entity = result.get();
            entity.togglePinned(); // pinned 상태 변경
            repository.save(entity);
        } else {
            throw new RuntimeException("Notice not found");
        }
    }

    private BooleanBuilder getSearch(NpageRequestDTO requestDTO) {
        String type = requestDTO.getType();
        BooleanBuilder booleanBuilder = new BooleanBuilder(); // where
        QNotice qnotice = QNotice.notice;
        String keyword = requestDTO.getKeyword();
        BooleanExpression expression = qnotice.nno.gt(0L); // nno > 0 조건만 생성
        booleanBuilder.and(expression); // where nno > 0
        if (type == null || type.trim().length() == 0) {
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

}

