package com.top.security.handler;

import com.top.entity.Member;
import com.top.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;

    public CustomLoginSuccessHandler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email);

        if (member != null && member.isSocial() && (member.getPhone() == null || member.getAddress() == null)) {
            // 소셜 회원이면서 필수 정보가 없는 경우
            request.getSession().setAttribute("member", member);
            getRedirectStrategy().sendRedirect(request, response, "/members/add-social-info");
        } else {
            // 그 외 경우
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }



}
