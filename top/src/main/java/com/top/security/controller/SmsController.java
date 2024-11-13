package com.top.security.controller;

import com.top.security.dto.SmsRequestDto;
import com.top.security.service.SmsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    @Autowired
    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    // 인증 코드 발송
    @PostMapping("/send")
    public ResponseEntity<String> sendSMS(@RequestBody @Valid SmsRequestDto smsRequestDto, HttpSession session) {
        String certificationCode = smsService.sendSms(smsRequestDto);
        session.setAttribute("certificationCode", certificationCode);
        session.setAttribute("phoneNum", smsRequestDto.getPhoneNum());
        session.setAttribute("redirectPath", smsRequestDto.getRedirectPath());
        return ResponseEntity.ok("인증 코드를 전송했습니다.");
    }

    // 인증 코드 검증 후 리다이렉트
    @PostMapping("/verifyCode")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> requestData, HttpSession session) {
        String verificationCode = requestData.get("verificationCode");
        String savedCertificationCode = (String) session.getAttribute("certificationCode");

        if (savedCertificationCode != null && savedCertificationCode.equals(verificationCode)) {
            session.setAttribute("verifiedPhone", session.getAttribute("phoneNum"));
            String redirectPath = (String) session.getAttribute("redirectPath");
            session.removeAttribute("redirectPath");
            return ResponseEntity.ok(redirectPath != null ? redirectPath : "/members/foundId");
        } else {
            return ResponseEntity.status(400).body("인증 코드가 올바르지 않습니다.");
        }
    }

    // 비밀번호 찾기용 인증 코드 발송
    @PostMapping("/sendForPasswordReset")
    public ResponseEntity<String> sendSMSForPasswordReset(@RequestBody @Valid SmsRequestDto smsRequestDto, HttpSession session) {
        String certificationCode = smsService.sendSms(smsRequestDto);
        session.setAttribute("certificationCode", certificationCode);
        session.setAttribute("phoneNum", smsRequestDto.getPhoneNum());
        session.setAttribute("email", smsRequestDto.getEmail());
        return ResponseEntity.ok("인증 코드를 전송했습니다.");
    }

    // 비밀번호 찾기용 인증 코드 검증 및 이메일 확인
    @PostMapping("/verifyCodeForPasswordReset")
    public ResponseEntity<String> verifyCodeForPasswordReset(@RequestBody Map<String, String> requestData, HttpSession session) {
        String verificationCode = requestData.get("verificationCode");
        String email = requestData.get("email");
        String phone = requestData.get("phone");
        String savedCertificationCode = (String) session.getAttribute("certificationCode");
        String savedEmail = (String) session.getAttribute("email");
        String savedPhone = (String) session.getAttribute("phoneNum");

        // 인증 코드, 이메일, 휴대폰 번호가 모두 일치하는 경우에만 인증 성공
        if (savedCertificationCode != null && savedCertificationCode.equals(verificationCode)
                && savedEmail.equals(email) && savedPhone.equals(phone)) {
            session.setAttribute("verifiedPhone", phone);
            session.setAttribute("verifiedEmail", email);
            return ResponseEntity.ok("valid");
        } else {
            return ResponseEntity.status(400).body("inValid");
        }
    }

    // 휴대폰 인증 페이지 반환 (HTML 뷰 반환)
    @GetMapping("/verify")
    public String phoneVerificationPage() {
        return "member/phoneVerificationForm"; // Thymeleaf 템플릿을 반환
    }
}
