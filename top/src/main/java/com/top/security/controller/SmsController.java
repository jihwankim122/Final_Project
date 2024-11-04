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

    // 인증 코드 발송 (JSON 응답)
    @PostMapping("/send")
    public ResponseEntity<String> sendSMS(@RequestBody @Valid SmsRequestDto smsRequestDto, HttpSession session) {
        String certificationCode = smsService.sendSms(smsRequestDto);
        session.setAttribute("certificationCode", certificationCode);
        session.setAttribute("phoneNum", smsRequestDto.getPhoneNum());
        return ResponseEntity.ok("인증 코드를 전송했습니다.");
    }

    // 인증 코드 검증 (JSON 응답)
    @PostMapping("/verifyCode")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> requestData, HttpSession session) {
        String verificationCode = requestData.get("verificationCode");
        String savedCertificationCode = (String) session.getAttribute("certificationCode");

        if (savedCertificationCode != null && savedCertificationCode.equals(verificationCode)) {
            String phoneNum = (String) session.getAttribute("phoneNum");
            session.setAttribute("verifiedPhone", phoneNum);
            return ResponseEntity.ok("인증에 성공했습니다. 회원가입 페이지로 이동합니다.");
        } else {
            return ResponseEntity.status(400).body("인증 코드가 올바르지 않습니다.");
        }
    }


    // 휴대폰 인증 페이지 반환 (HTML 뷰 반환)
    @GetMapping("/verify")
    public String phoneVerificationPage() {
        return "member/phoneVerificationForm"; // Thymeleaf 템플릿을 반환
    }

    // 인증 코드 발송 - 템플릿 페이지 내의 AJAX 요청을 위한 메서드 예시
    @GetMapping("/sendPage")
    public String sendVerificationPage() {
        return "member/phoneVerificationForm";
    }
}
