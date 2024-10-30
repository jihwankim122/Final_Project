package com.top.security.service;

import com.top.constant.Role;
import com.top.entity.Member;
import com.top.repository.MemberRepository;
import com.top.security.dto.ClubAuthMemberDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    private boolean isSocial; // 소셜 로그인 여부 저장
    private final MemberRepository memberRepository;
    private final HttpSession httpSession; // 세션 주입

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 네이버와 다른 소셜 제공자 구분하여 이메일, 이름 추출
        Map<String, Object> userAttributes = extractUserAttributes(provider, attributes);
        String email = (String) userAttributes.get("email");
        String name = (String) userAttributes.getOrDefault("name", "Unknown");
        String nickname = name; // 이름을 닉네임으로 사용

        // 사용자 생성 또는 조회
        Member member = findOrCreateMember(email, name, nickname);

        // 세션에 사용자 정보 저장
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        request.getSession().setAttribute("member", member);

        // OAuth2User로 반환
        return new ClubAuthMemberDto(email, nickname, name, true, attributes);
    }

    private Map<String, Object> extractUserAttributes(String provider, Map<String, Object> attributes) {
        if ("naver".equals(provider)) {
            // 네이버의 중첩된 응답에서 'response' 부분 추출
            return (Map<String, Object>) attributes.get("response");
        } else if ("kakao".equals(provider)) {
            // 카카오의 경우 'kakao_account'에서 정보 추출
            return (Map<String, Object>) attributes.get("kakao_account");
        }
        // 기본 제공자 (예: 구글)
        return attributes;
    }


    private String extractEmail(String provider, Map<String, Object> attributes) {
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("email");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return (String) kakaoAccount.get("email");
        }
        return (String) attributes.get("email");  // 구글의 경우
    }


    private String extractName(String provider, Map<String, Object> attributes) {
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("name");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> profile = (Map<String, Object>) attributes.get("profile");
            return profile != null ? (String) profile.get("nickname") : "Unknown";
        }
        return (String) attributes.getOrDefault("name", "Unknown");  // 구글의 경우
    }

    private String extractNickname(String provider, Map<String, Object> attributes) {
        return extractName(provider, attributes); // 기본적으로 이름을 닉네임으로 사용
    }

    private Member findOrCreateMember(String email, String name, String nickname) {
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            // 사용자 정보가 없으면 새로 생성
            member = createNewMember(email, name, nickname);
            memberRepository.save(member);
            System.out.println("Creating new member with email: " + email);
        }

        return member;
    }

    private Member createNewMember(String email, String name, String nickname) {
        Member member = new Member();
        member.setEmail(email);
        member.setName(name);
        member.setNickname(nickname);
        member.setRole(Role.USER);
        member.setPassword(null);
        member.setAddress(null);
        member.setCreatedBy(null);
        member.setModifiedBy(null);
        return member;
    }
}
