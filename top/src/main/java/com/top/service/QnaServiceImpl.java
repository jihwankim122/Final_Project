package com.top.service;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.top.dto.NpageRequestDTO;
import com.top.dto.NpageResultDTO;
import com.top.dto.QnaDTO;
import com.top.entity.Member;
import com.top.entity.QQna;
import com.top.entity.Qna;
import com.top.repository.MemberRepository;
import com.top.repository.QnaRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class QnaServiceImpl implements QnaService {

    private final QnaRepository repository;
    private final MemberRepository memberRepository; // 28 Oct

    // Regist
    @Override
    public Long register(QnaDTO dto) {
        log.info("DTO------------------------");
        log.info(dto);
//        /////////////////////////
        Member member = memberRepository.findByEmail(dto.getWriter());
        if (member == null) {
            throw new RuntimeException("Member not found");
        }

        // Call Creating Entitiy
        Qna entity = dtoToEntity(dto, member);

        log.info(entity);
        repository.save(entity); // Regist
        return entity.getQno(); // List Number
    }

    @Override
    public NpageResultDTO<QnaDTO, Qna> getList(NpageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("qno").descending()); // order by nno desc limit 0,10
        BooleanBuilder booleanBuilder = getSearch(requestDTO);

        Page<Qna> result = repository.findAll(booleanBuilder, pageable); // Using Querydsl

        // 25 Oct ------------------------------------------------------------------------------------
        Function<Qna, QnaDTO> fn = (entity) -> {
            QnaDTO dto = entityToDto(entity); // Convert entity to DTO
            Member member = memberRepository.findByEmail(dto.getWriter()); // Member From Email
            if (member != null) {
                dto.setWriterName(member.getName()); // 25 Oct
            }
            // true
            dto.setHasAnswer(entity.hasAnswers()); // Setting Answers 01 Nov
            return dto;

        };

        return new NpageResultDTO<>(result, fn);
    }



    private BooleanBuilder getSearch(NpageRequestDTO requestDTO) {
        String type = requestDTO.getType(); // 검색필드
        BooleanBuilder booleanBuilder = new BooleanBuilder(); // where
        QQna qnotice = QQna.qna;
        String keyword = requestDTO.getKeyword(); // 검색어
        BooleanExpression expression = qnotice.qno.gt(0L); // qno > 0
        booleanBuilder.and(expression); // where qno > 0
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
    public QnaDTO read(Long qno) {
        Optional<Qna> result = repository.findById(qno);
        return result.isPresent() ? entityToDto(result.get()) : null;
    }

    public Qna getQnaById(Long qno) {
        return repository.findById(qno)
                .orElseThrow(() -> new EntityNotFoundException("Qna not found for ID: " + qno)); // Qna 엔티티 반환
    }



    // Update
    @Override
    public void modify(QnaDTO dto) {
        //업데이트 하는 항목은 '제목', '내용'
        Optional<Qna> result = repository.findById(dto.getQno());
        if (result.isPresent()) {
            Qna entity = result.get();
            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());
            repository.save(entity);
        }
    }
    // Delete
    @Override
    public void remove(Long qno) {
        repository.deleteById(qno);
    }

}

