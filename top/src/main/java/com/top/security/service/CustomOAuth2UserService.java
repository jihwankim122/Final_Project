package com.top.security.service;

import com.top.constant.Grade;
import com.top.constant.Role;
import com.top.entity.Member;
import com.top.repository.MemberRepository;
import com.top.security.dto.ClubAuthMemberDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    @Getter
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId();

        String email = extractEmail(provider, attributes);
        String name = extractName(provider, attributes);
        String nickname = extractNickname(provider, attributes);

        Member member = findOrCreateMember(email, name, nickname);

        // 세션에 Member 엔터티 저장
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        request.getSession().setAttribute("member", member);

        return new ClubAuthMemberDto(email, nickname, name, true, attributes);
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return (String) attributes.get("email");
            case "naver":
                Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
                return (String) naverResponse.get("email");
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                return (String) kakaoAccount.get("email");
            default:
                throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }
    }

    private String extractName(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return (String) attributes.get("name");
            case "naver":
                Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
                return (String) naverResponse.get("name");
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                return profile != null ? (String) profile.get("nickname") : "Unknown";
            default:
                throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }
    }

    private String extractNickname(String provider, Map<String, Object> attributes) {
        return extractName(provider, attributes);
    }

    // CustomOAuth2UserService.java
    private Member findOrCreateMember(String email, String name, String nickname) {
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            // 소셜 회원 생성 메서드 사용
            member = Member.createSocialMember(email, name, nickname);
            member.setSocial(true);
            memberRepository.save(member);
            System.out.println("Creating new social member with email: " + email);
        }

        return member;
    }

    private Member createNewMember(String email, String name, String nickname) {
        Member member = new Member();
        member.setEmail(email);
        member.setName(name);
        member.setNickname(nickname);
        //241105 은열 추가
        member.setGrade(Grade.BRONZE);
        member.setRole(Role.USER);
        member.setPassword(null);
        member.setAddress(null);
        member.setCreatedBy(null);
        member.setModifiedBy(null);
        member.setSocial(true);
        return member;
    }

}
