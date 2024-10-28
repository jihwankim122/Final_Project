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

    private final MemberRepository memberRepository;
    private final HttpSession httpSession; // 세션 주입

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId();

        String email = extractEmail(provider, attributes);
        String name = extractName(provider, attributes);
        String nickname = extractNickname(provider, attributes);

        Member member = findOrCreateMember(email, name, nickname);

        // 세션 설정
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("member", member);

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
                String email = (String) kakaoAccount.get("email");
                if (email == null) {
                    throw new OAuth2AuthenticationException("카카오 로그인: 이메일 정보가 없습니다.");
                }
                return email;
            default:
                throw new OAuth2AuthenticationException("지원되지 않는 제공자: " + provider);
        }
    }


    private String extractName(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
            case "naver":
                return (String) attributes.getOrDefault("name", "Unknown");
            case "kakao":
                Map<String, Object> profile = (Map<String, Object>) attributes.get("profile");
                return profile != null ? (String) profile.getOrDefault("nickname", "Unknown") : "Unknown";
            default:
                throw new OAuth2AuthenticationException("지원되지 않는 제공자: " + provider);
        }
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
