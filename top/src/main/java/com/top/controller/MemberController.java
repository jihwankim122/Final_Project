package com.top.controller;

import com.top.dto.MemberFormDto;
import com.top.security.dto.MemberUpdateFormDto;
import com.top.entity.Member;
import com.top.service.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController extends MemberBasicController {

    private final MemberServiceImpl memberService;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session; // 세션 주입

    @GetMapping(value = "/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/members/login";
    }

    @GetMapping(value = "/login")
    public String loginMember() {
        return "/member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }

    // SecurityContextHolder 사용해 로그인된 사용자의 이메일 가져오기
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // 비밀번호 확인 페이지로 이동
    @GetMapping(value = "/check-password")
    public String checkPasswordForm() {
        return "member/checkPassword";
    }

    // 비밀번호 확인 처리
    @PostMapping(value = "/check-password")
    public String checkPassword(@RequestParam("password") String password, Model model) {
        String email = getCurrentUserEmail();
        Member member = memberService.findByEmail(email);

        if (member == null || !passwordEncoder.matches(password, member.getPassword())) {
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "member/checkPassword";
        }

        // 비밀번호가 일치하면 세션에 확인 상태 저장 후 회원 정보 수정 페이지로 이동
        session.setAttribute("passwordVerified", true);
        return "redirect:/members/update";
    }

    @GetMapping(value = "/update")
    public String updateMemberForm(Model model) {
        Boolean passwordVerified = (Boolean) session.getAttribute("passwordVerified");
        if (passwordVerified == null || !passwordVerified) {
            return "redirect:/members/check-password";
        }

        String email = getCurrentUserEmail();
        Member member = memberService.findByEmail(email);

        MemberUpdateFormDto formDto = new MemberUpdateFormDto();
        formDto.setName(member.getName());
        formDto.setUsername(member.getId().toString());
        formDto.setEmail(member.getEmail());
        formDto.setAddress(member.getAddress());
        formDto.setPostcode(member.getPostcode());
        formDto.setDetailAddress(member.getDetailAddress());
        formDto.setPhone(member.getPhone());

        model.addAttribute("memberUpdateFormDto", formDto);
        return "member/updateMemberForm";
    }

    @PostMapping(value = "/update")
    public String updateMember(@Valid MemberUpdateFormDto formDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "member/updateMemberForm";
        }

        if (!formDto.getPassword().equals(formDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "비밀번호가 일치하지 않습니다.");
            return "member/updateMemberForm";
        }

        try {
            String email = getCurrentUserEmail();
            Member member = memberService.findByEmail(email);

            memberService.updateMember(
                    member, formDto.getEmail(), formDto.getPassword(),
                    formDto.getAddress(), formDto.getPostcode(),
                    formDto.getDetailAddress(), formDto.getPhone()
            );
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원 정보 수정 중 오류가 발생했습니다.");
            return "member/updateMemberForm";
        }

        // 수정 완료 후 세션의 비밀번호 확인 상태 초기화
        session.removeAttribute("passwordVerified");
        return "redirect:/";
    }
}
