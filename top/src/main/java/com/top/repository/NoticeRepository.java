package com.top.repository;

import com.top.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface NoticeRepository extends JpaRepository<Notice, Long>, QuerydslPredicateExecutor<Notice> {
}
