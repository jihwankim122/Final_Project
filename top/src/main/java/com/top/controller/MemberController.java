package com.top.controller;

import com.top.dto.MemberFormDto;
import com.top.security.dto.MemberUpdateFormDto;
import com.top.entity.Member;
import com.top.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberServiceImpl memberService;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 폼 이동
    @GetMapping(value = "/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    // 회원가입 처리
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

        return "redirect:/";
    }

    // 로그인 페이지 이동
    @GetMapping(value = "/login")
    public String loginMember() {
        return "/member/memberLoginForm";
    }

    // 로그인 실패 시 오류 메시지 표시
    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }

    // 회원 정보 수정 폼 이동
    @GetMapping(value = "/update")
    public String updateMemberForm(Principal principal, Model model) {
        Member member = memberService.findByEmail(principal.getName());

        if (member == null) {
            throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
        }

        // DTO로 변환 후 전달
        MemberUpdateFormDto formDto = new MemberUpdateFormDto();
        formDto.setName(member.getName()); // 이름은 수정 불가
        formDto.setUsername(member.getId().toString()); // ID는 수정 불가
        formDto.setEmail(member.getEmail());
        formDto.setAddress(member.getAddress());

        model.addAttribute("memberUpdateFormDto", formDto);
        return "member/updateMemberForm";
    }

    // 회원 정보 수정 처리
    @PostMapping(value = "/update")
    public String updateMember(
            @Valid MemberUpdateFormDto formDto, BindingResult bindingResult,
            Principal principal, Model model) {

        if (bindingResult.hasErrors()) {
            return "member/updateMemberForm";
        }

        if (!formDto.getPassword().equals(formDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "비밀번호가 일치하지 않습니다.");
            return "member/updateMemberForm";
        }

        try {
            Member member = memberService.findByEmail(principal.getName());
            if (member == null) {
                throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
            }

            // 비밀번호 및 주소 업데이트
            member.setPassword(passwordEncoder.encode(formDto.getPassword()));
            member.setEmail(formDto.getEmail());
            member.setAddress(formDto.getAddress());

            memberService.saveMember(member);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원 정보 수정 중 오류가 발생했습니다.");
            return "member/updateMemberForm";
        }

        return "redirect:/";
    }
}
//1024 유진 전체 복붙해서 수정