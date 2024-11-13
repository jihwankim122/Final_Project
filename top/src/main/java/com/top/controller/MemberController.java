package com.top.controller;

import com.top.dto.MemberFormDto;
import com.top.repository.MemberRepository;
import com.top.security.dto.MemberUpdateFormDto;
import com.top.entity.Member;
import com.top.service.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController extends MemberBasicController {

    private final MemberServiceImpl memberService;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session;
    private final MemberRepository memberRepository;

    // 회원가입 폼
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

    // 회원가입
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

    // 로그인 페이지
    @GetMapping(value = "/login")
    public String loginMember() {
        return "/member/memberLoginForm";
    }

    // 로그인 에러 페이지
    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }

    // 현재 로그인된 사용자 이메일 가져오기
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // 비밀번호 확인 폼
    @GetMapping(value = "/check-password")
    public String checkPasswordForm(HttpSession session, Principal principal) {
        // 세션에서 isSocialUser 플래그를 확인
        System.out.println(principal.getName());
        Member member=memberRepository.findByEmail(principal.getName());

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

    // 회원 정보 수정 폼
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

    // 회원 정보 수정 처리
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

    // 소셜 회원 추가 정보 입력 폼
    @GetMapping("/add-social-info")
    public String addSocialInfoForm(Model model) {
        model.addAttribute("memberUpdateFormDto", new MemberUpdateFormDto());
        return "member/addSocialInfoForm";
    }

    // 소셜 회원 추가 정보 입력 처리
    @PostMapping("/add-social-info")
    public String addSocialInfo(MemberUpdateFormDto formDto, Model model) {
        String email = getCurrentUserEmail();
        Member member = memberService.findByEmail(email);

        memberService.updateMember(
                member, member.getEmail(), null,
                formDto.getAddress(), formDto.getPostcode(),
                formDto.getDetailAddress(), formDto.getPhone()
        );

        return "redirect:/";
    }

//    @GetMapping("/checkEmail")
//    public String checkEmail(@RequestParam("email") String email, Model model) {
//        boolean isAvailable = memberService.isEmailAvailable(email);
//        int result = isAvailable ? 0 : 1; // 사용 가능하면 0, 이미 사용 중이면 1
//        model.addAttribute("email", email);
//        model.addAttribute("result", memberService.isEmailAvailable(email));
//        return "member/checkEmail";
//    }

    @RequestMapping (value="/checkEmail",method = {RequestMethod.POST})
    @ResponseBody
    public  ResponseEntity<String> checkEmail(@RequestParam("email") String email) {
        String isAvailable = String.valueOf(memberService.isEmailAvailable(email));
        return new ResponseEntity<>(isAvailable,HttpStatus.OK);
    }

    // 아이디 찾기 폼
    @GetMapping("/findIdForm")
    public String findIdForm() {
        return "member/findIdForm";
    }

    @GetMapping("/foundId")
    public String foundIdForm(Model model) {
        String phone = (String) session.getAttribute("verifiedPhone");
        session.removeAttribute("verifiedPhone");
        model.addAttribute("email",memberService.findByPhone(phone).getEmail());
        return "member/foundId";
    }
    // 아이디 찾기 처리
    @PostMapping("/foundId")
    public String findIdByPhone(@RequestParam("phone") String phone, Model model) {
        try {
            Member member = memberService.findByPhone(phone);
            model.addAttribute("email", member.getEmail());
            return "member/foundId";
        } catch (UsernameNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/findIdForm";
        }
    }

    // 비밀번호 찾기 폼
    @GetMapping("/findPassword")
    public String findPasswordForm() {
        return "member/findPasswordForm";
    }

    // 비밀번호 찾기 처리
    @PostMapping("/findPassword")
    public String findPassword(@RequestParam("email") String email, @RequestParam("phone") String phone, HttpSession session, Model model) {
        try {
            Member member = memberService.verifyMemberByEmailAndPhone(email, phone);
            session.setAttribute("verifiedMemberEmail", member.getEmail()); // 인증된 회원 정보 세션에 저장
            return "redirect:/members/reset-password"; // 인증 성공 시 비밀번호 재설정 페이지로 리다이렉트
        } catch (UsernameNotFoundException e) {
            model.addAttribute("errorMessage", "입력한 정보로 가입된 회원을 찾을 수 없습니다.");
            return "member/findPasswordForm";
        }
    }

    // 비밀번호 재설정 폼
    @GetMapping("/reset-password")
    public String resetPasswordForm(HttpSession session, Model model) {
        String email = (String) session.getAttribute("verifiedMemberEmail");
        if (email == null) {
            return "redirect:/members/findPassword"; // 이메일 인증이 없다면 다시 찾기 폼으로 리다이렉트
        }
        model.addAttribute("email", email);
        return "member/resetPasswordForm";
    }

    // 비밀번호 재설정 처리
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("newPassword") String newPassword, HttpSession session, Model model) {
        String email = (String) session.getAttribute("verifiedMemberEmail");
        if (email == null) {
            return "redirect:/members/findPassword"; // 인증되지 않은 상태에서 접근 시 찾기 폼으로 리다이렉트
        }

        boolean result = memberService.resetPassword(email, newPassword);
        session.removeAttribute("verifiedMemberEmail"); // 인증 정보 세션에서 제거

        if (result) {
            return "redirect:/members/login"; // 비밀번호 재설정 성공 시 로그인 페이지로 리다이렉트
        } else {
            model.addAttribute("errorMessage", "비밀번호 재설정 중 오류가 발생했습니다.");
            return "member/resetPasswordForm";
        }
    }
}