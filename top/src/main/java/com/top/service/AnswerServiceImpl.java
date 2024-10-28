package com.top.service;

import com.top.dto.AnswerDTO;
import com.top.entity.Answer;
import com.top.entity.Qna;
import com.top.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    // Regist
    @Override
    public Long register(AnswerDTO dto) {
        log.info("DTO for registration: {}", dto);
        Answer entity = dtoToEntity(dto, new Qna(dto.getQid())); // Assuming Qna can be constructed this way
        answerRepository.save(entity);
        return entity.getId();
    }

    // Get list of answers
    @Override
    public List<AnswerDTO> getList(Qna qna) {
        List<Answer> answers = answerRepository.findByQna(qna); // Assuming a method in the repository
        return answers.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    // Update
    @Override
    public void modify(AnswerDTO dto) {
        Optional<Answer> result = answerRepository.findById(dto.getId());
        if (result.isPresent()) {
            Answer entity = result.get();
            entity.update(dto.getContent());
            answerRepository.save(entity);
        } else {
            log.warn("Answer not found for ID: {}", dto.getId());
        }
    }

    // Delete
    @Override
    public void remove(Long id) {
        answerRepository.deleteById(id);
    }
}
