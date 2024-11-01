package com.top.service;

import com.top.entity.Member;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {
    Member saveMember(Member member);
    Member findByEmail(String email); // 1101 성아 추가 // 이메일로 회원 조회
}
