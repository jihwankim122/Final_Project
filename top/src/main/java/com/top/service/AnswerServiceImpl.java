package com.top.service;

import com.top.dto.AnswerDTO;
import com.top.entity.Answer;
import com.top.entity.Member;
import com.top.entity.Qna;
import com.top.repository.AnswerRepository;
import com.top.repository.MemberRepository;
import com.top.repository.QnaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QnaRepository qnaRepository; // 28 Oct
    private final MemberRepository memberRepository; // MemberRepository 주입

    // Regist
    public Long register(AnswerDTO dto) {
        log.info("DTO for registration: {}", dto);

        // Qna 객체를 DB에서 조회
        Optional<Qna> qnaOpt = qnaRepository.findById(dto.getQid());
        if (qnaOpt.isPresent()) {
            // Qna가 존재하는 경우 DTO를 엔티티로 변환
            Answer entity = dtoToEntity(dto, qnaOpt.get());

            // Member ID를 통해 Member 객체를 조회하고 설정
            if (dto.getMid() != null) { // 수정된 부분
                Member member = memberRepository.findById(dto.getMid())
                        .orElseThrow(() -> new EntityNotFoundException("Member not found for ID: " + dto.getMid()));
                entity.setMember(member);// Set Writer
                dto.setWriterName(member.getName());// Set WriterName
            } else {
                throw new IllegalArgumentException("Member ID must not be null.");
            }

            // Answer 엔티티 저장
            answerRepository.save(entity);
            return entity.getId();
        } else {
            throw new EntityNotFoundException("Qna not found for ID: " + dto.getQid());
        }
    }

    // Get list of answers
    @Override
    public List<AnswerDTO> getList(Qna qna) {
        List<Answer> answers = answerRepository.findByQna(qna); // Assuming a method in the repository
        return answers.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }


    // Delete
    @Override
    public void remove(Long id) {
        answerRepository.deleteById(id);
    }
}
