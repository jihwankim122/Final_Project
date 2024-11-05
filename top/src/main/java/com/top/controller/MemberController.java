package com.top.controller;

import com.top.dto.MemberFormDto;
import com.top.repository.MemberRepository;
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

import java.security.Principal;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController extends MemberBasicController {

    private final MemberServiceImpl memberService;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session;
    private final MemberRepository memberRepository;

    @GetMapping(value = "/new")
    public String memberForm(Model model) {
        MemberFormDto memberFormDto = new MemberFormDto();
        String verifiedPhone = (String) session.getAttribute("verifiedPhone");

        if (verifiedPhone == null) {
            return "redirect:/sms/verify";
        }

        memberFormDto.setPhone(verifiedPhone);
        model.addAttribute("memberFormDto", memberFormDto);
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

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping(value = "/check-password")
    public String checkPasswordForm(HttpSession session, Principal principal) {
        // 세션에서 isSocialUser 플래그를 확인
        System.out.println(principal.getName());
        Member member=memberRepository.findByEmail(principal.getName());


//        Boolean isSocialUser = (Boolean) session.getAttribute("isSocialUser");
        // 소셜 회원이라면 비밀번호 입력 없이 바로 추가 정보 입력 페이지로 리다이렉트
        if (member.isSocial()) {
            return "redirect:/members/add-social-info";
        }
        return "member/checkPassword"; // 일반 회원은 비밀번호 확인 페이지로 이동
    }

    // 비밀번호 확인 처리
    @PostMapping(value = "/check-password")
    public String checkPassword(@RequestParam(value = "password", required = false) String password, Model model, HttpSession session) {
        Boolean isSocialUser = (Boolean) session.getAttribute("isSocialUser");

        if (Boolean.TRUE.equals(isSocialUser)) {
            return "redirect:/members/add-social-info";
        }

        String email = getCurrentUserEmail();
        Member member = memberService.findByEmail(email);

        if (member == null) {
            model.addAttribute("errorMessage", "회원 정보를 찾을 수 없습니다.");
            return "member/checkPassword";
        }

        if (!passwordEncoder.matches(password, member.getPassword())) {
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "member/checkPassword";
        }

        session.setAttribute("passwordVerified", true);
        return "redirect:/members/update";
    }

    @GetMapping("/update")
    public String updateMemberForm(Model model, HttpSession session) {
        Boolean isSocialUser = (Boolean) session.getAttribute("isSocialUser");
        String email = getCurrentUserEmail();
        Member member = memberService.findByEmail(email);

        if (Boolean.TRUE.equals(isSocialUser)) {
            return "redirect:/members/add-social-info";
        }

        Boolean passwordVerified = (Boolean) session.getAttribute("passwordVerified");
        if (passwordVerified == null || !passwordVerified) {
            return "redirect:/members/check-password";
        }

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

        session.removeAttribute("passwordVerified");
        return "redirect:/";
    }

    @GetMapping("/add-social-info")
    public String addSocialInfoForm(Model model) {
        model.addAttribute("memberUpdateFormDto", new MemberUpdateFormDto());
        return "member/addSocialInfoForm";
    }

    @PostMapping("/add-social-info")
//    public String addSocialInfo(@Valid MemberUpdateFormDto formDto, BindingResult bindingResult, Model model) {
    public String addSocialInfo(MemberUpdateFormDto formDto, Model model) {
//        if (bindingResult.hasErrors()) {
//            return "member/addSocialInfoForm";
//        }

        String email = getCurrentUserEmail();
        Member member = memberService.findByEmail(email);

        memberService.updateMember(
                member, member.getEmail(), null,
                formDto.getAddress(), formDto.getPostcode(),
                formDto.getDetailAddress(), formDto.getPhone()
        );

        return "redirect:/";
    }
}
