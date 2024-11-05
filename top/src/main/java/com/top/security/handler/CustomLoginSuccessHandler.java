package com.top.security.handler;

import com.top.entity.Member;
import com.top.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email);

        if (member != null && member.isSocial()) {
            // 세션에 소셜 회원 여부 플래그 설정
            request.getSession().setAttribute("isSocialUser", true);
            super.onAuthenticationSuccess(request, response, authentication);
            // 소셜 회원이고 비밀번호가 없는 경우 추가 정보 입력 페이지로 리다이렉트
//            if (member.getPassword() == null) {
//                request.getSession().setAttribute("member", member);
//                getRedirectStrategy().sendRedirect(request, response, "/members/add-social-info");
//            } else {
//                super.onAuthenticationSuccess(request, response, authentication);
//            }
        } else {
            // 일반 회원의 경우 플래그를 false로 설정
            request.getSession().setAttribute("isSocialUser", false);
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
