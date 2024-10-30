package com.top.repository;

import com.top.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository를 통해 Member 엔터티에 대한 기본 CRUD 기능 제공
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 회원 존재 여부 확인하는 메서드 선언
    boolean existsByEmail(String email);

    // 이메일로 회원 정보 조회
    Member findByEmail(String email);
}
