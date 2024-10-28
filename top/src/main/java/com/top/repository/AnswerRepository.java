package com.top.repository;

import com.top.entity.Answer;
import com.top.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQna(Qna qna);
}
