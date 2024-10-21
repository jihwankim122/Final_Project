package com.top.service;

import com.top.entity.Member;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {
    Member saveMember(Member member);
}
