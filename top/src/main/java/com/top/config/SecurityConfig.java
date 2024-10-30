package com.top.config;

import com.top.repository.MemberRepository;
import com.top.security.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository; // 회원 저장소 주입
    private final HttpSession httpSession; // 세션 주입

    // SecurityFilterChain 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/members/update", "/members/profile").permitAll() // 메인, 수정 페이지 접근 허용
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll() // 정적 리소스 허용
                        .requestMatchers("/members/**", "/item/**", "/images/**").permitAll() // 회원 관련 경로 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 권한 설정
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/members/login") // 사용자 정의 로그인 페이지
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 메인 페이지로 이동
                        .usernameParameter("email") // 로그인에 이메일 사용
                        .failureUrl("/members/login/error") // 로그인 실패 시 오류 페이지
                        .failureHandler(new CustomAuthenticationFailureHandler()) // 로그인 실패 핸들러
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) // 로그아웃 경로 설정
                        .logoutSuccessUrl("/") // 로그아웃 후 메인 페이지로 이동
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/members/login") // OAuth2 로그인 페이지 설정
                        .defaultSuccessUrl("/", true) // OAuth2 로그인 성공 시 메인 페이지로 이동
                        .failureUrl("/members/login/error") // OAuth2 로그인 실패 시 오류 페이지
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauth2UserService()) // 사용자 정보 처리 서비스
                        )
                );

        return http.build(); // SecurityFilterChain 빌드
    }

    // 비밀번호 암호화기를 빈으로 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 사용
    }

    // OAuth2 사용자 서비스 빈 정의
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new CustomOAuth2UserService(memberRepository, httpSession); // 사용자 정보 처리 서비스
    }
}
