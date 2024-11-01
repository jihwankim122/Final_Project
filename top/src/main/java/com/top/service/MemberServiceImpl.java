package com.top.service;

import com.top.entity.Member;
import com.top.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 저장
    @Override
    public Member saveMember(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        return memberRepository.save(member);
    }

    // 중복 회원 검증
    private void validateDuplicateMember(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    // 이메일로 사용자 로드 (UserDetails 반환)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = findByEmail(email); // 예외 처리된 메서드 사용
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

    // 이메일로 회원 조회 (회원 정보 없을 시 예외 처리)
    @Override
    public Member findByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("회원 정보를 찾을 수 없습니다: " + email);
        }
        return member;
    }

    // 회원 정보 수정 메서드
    public void updateMember(
            Member member, String email, String password,
            String address, String postcode, String detailAddress, String phone) {

        member.setEmail(email); // 이메일 설정
        member.setAddress(address); // 주소 설정
        member.setPostcode(postcode); // 우편번호 설정
        member.setDetailAddress(detailAddress); // 상세주소 설정
        member.setPhone(phone); // 전화번호 설정

        // 비밀번호가 입력된 경우 암호화 후 설정
        if (password != null && !password.isEmpty()) {
            member.setPassword(passwordEncoder.encode(password));
        }

        memberRepository.save(member); // 수정된 회원 정보 저장
    }

    // 회원의 존재 여부를 확인하는 메서드 추가
    public boolean memberExists(String email) {
        return memberRepository.existsByEmail(email);
    }

}
