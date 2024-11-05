package com.top.controller;


import com.top.entity.Member;
import com.top.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;

    @GetMapping("/main")
    public String adminMain() {
        return "admin/adminMain";
    }

    @GetMapping("/memberinfo")
    public String getAllMembers(Model model) {
        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        return "admin/memberInfo";
    }

    @PostMapping("/updateMembers")
    public String updateMembers(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        try {
            for (String key : params.keySet()) {
                if (key.startsWith("grade_")) {
                    // memberId는 grade_ 뒤의 숫자 부분을 추출
                    Long memberId = Long.parseLong(key.substring(6)); // "grade_" 이후의 숫자를 추출, memberid=회원번호(순서)
                    String grade = params.get(key); // 선택된 등급
                    memberService.updateMemberGrade(memberId, grade); // 회원 등급 업데이트
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "회원 정보가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "회원 정보 업데이트 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/memberinfo"; // 업데이트 후 다시 회원 정보 페이지로 리다이렉트
    }
}