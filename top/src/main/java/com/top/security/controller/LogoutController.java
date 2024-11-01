package com.top.security.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LogoutController {

    // 일반 로그아웃 요청 처리
    @GetMapping("/members/logout")
    public String normalLogout(HttpServletRequest request, HttpServletResponse response) {
        // 세션 무효화 및 쿠키 삭제
        invalidateSessionAndCookies(request, response);
        return "redirect:/";
    }

    // 소셜 로그아웃 요청 처리
    @GetMapping("/custom-logout")
    public String socialLogout(@RequestParam("provider") String provider, HttpServletRequest request, HttpServletResponse response) {
        // 세션 무효화 및 쿠키 삭제
        invalidateSessionAndCookies(request, response);

        // 소셜 로그아웃 리다이렉트 URL 처리
        switch (provider) {
            case "google":
                return "redirect:https://accounts.google.com/Logout?continue=http://localhost:8888";
            case "naver":
                return "redirect:https://nid.naver.com/nidlogin.logout";
            case "kakao":
                return "redirect:https://kauth.kakao.com/oauth/logout?client_id=YOUR_CLIENT_ID&logout_redirect_uri=http://localhost:8888";
            default:
                return "redirect:/";
        }
    }

    // 세션과 쿠키를 무효화하는 메서드
    private void invalidateSessionAndCookies(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
    }
}
