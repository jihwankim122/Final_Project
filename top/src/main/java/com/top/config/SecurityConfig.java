package com.top.config;

import com.top.repository.MemberRepository;
import com.top.security.service.CustomOAuth2UserService;
import com.top.service.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
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
public class SecurityConfig {

    private final MemberServiceImpl memberService;
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    // 생성자 주입을 통한 의존성 주입
    public SecurityConfig(MemberServiceImpl memberService, MemberRepository memberRepository, HttpSession httpSession) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.httpSession = httpSession;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/", "/members/**", "/item/**", "/images/**").permitAll() // "/members/**" 경로 접근 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 권한 설정
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/members/login")
                        .defaultSuccessUrl("/", true)
                        .usernameParameter("email")
                        .failureUrl("/members/login/error")
                        .failureHandler(new CustomAuthenticationFailureHandler())
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                        .logoutSuccessUrl("/")
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/members/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/members/login/error")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauth2UserService())
                        )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new CustomOAuth2UserService(memberRepository, httpSession);
    }
}
