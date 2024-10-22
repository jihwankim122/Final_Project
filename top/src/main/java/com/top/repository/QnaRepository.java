package com.top.repository;


import com.top.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface QnaRepository extends JpaRepository<Qna, Long>, QuerydslPredicateExecutor<Qna> {
}
