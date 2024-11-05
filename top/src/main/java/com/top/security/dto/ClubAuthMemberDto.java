package com.top.security.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class ClubAuthMemberDto implements OAuth2User {

    private String email; // 사용자 이메일
    private String name; // 사용자 이름
    private String nickname; // 사용자 닉네임 추가
    private Map<String, Object> attributes; // OAuth2 사용자 속성
    private boolean isSocial; // 소셜 로그인 여부

    // 생성자
    public ClubAuthMemberDto(String email, String nickname, String name, boolean isSocial, Map<String, Object> attributes) {
        this.email = email;
        this.nickname = nickname; // 닉네임 초기화
        this.name = name;
        this.attributes = attributes;
        this.isSocial = isSocial;
    }

    // OAuth2User 인터페이스 메서드 구현
    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // 사용자 속성 반환
    }

    @Override
    public String getName() {
        // 카카오 로그인일 경우 닉네임 반환, 아니면 이메일 반환
//        return isSocial ? nickname : email;
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 소셜 로그인 사용자에게 기본 권한 부여
        return null; // 권한이 필요한 경우 구현
    }

    // Getter 및 Setter 추가
    public String getEmail() {
        return email;
    }

    public String getNickname() { // 닉네임 getter 추가
        return name;
    }

    public boolean isSocial() {
        return isSocial;
    }
}
