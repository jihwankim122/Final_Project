package com.top.config;

import com.top.repository.MemberRepository;
import com.top.security.handler.CustomLoginSuccessHandler;
import com.top.security.service.CustomOAuth2UserService;
import com.top.service.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Getter
    private final MemberServiceImpl memberService;
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;
    private final CustomLoginSuccessHandler loginSuccessHandler;
    private final DataSource dataSource;

    public SecurityConfig(@Lazy MemberServiceImpl memberService, MemberRepository memberRepository,
                          HttpSession httpSession, CustomLoginSuccessHandler loginSuccessHandler, DataSource dataSource) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.httpSession = httpSession;
        this.loginSuccessHandler = loginSuccessHandler;
        this.dataSource = dataSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/", "/members/**", "/item/**", "/images/**","/reviews/**","/notice/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/sms/**", "/members/login").permitAll() // 새로 추가된 접근 허용 경로
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/members/login")
                        .successHandler(loginSuccessHandler)
                        .usernameParameter("email")
                        .failureUrl("/members/login/error")
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecretKey") // 보안을 위해 랜덤한 키를 사용
                        .rememberMeParameter("remember-me") // HTML 폼에서 "remember-me"라는 이름의 필드를 사용
                        .tokenValiditySeconds(86400) // 1일(86400초) 동안 유지
                        .tokenRepository(persistentTokenRepository()) // 토큰을 저장할 저장소
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/members/login")
                        .successHandler(loginSuccessHandler)
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

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }
}

