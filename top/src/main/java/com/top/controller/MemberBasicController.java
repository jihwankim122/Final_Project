package com.top.controller;

import com.top.entity.Member;
import com.top.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class MemberBasicController {
    //member관련 카테고리를 제외한 모든 html로 member 객체 전달해주기 위해 만든 controller. 신규 controller가 생길 경우 상속해주면됨

    @Autowired
    private MemberRepository memberRepository;

    @ModelAttribute
    public void addMemberToModel(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Member member = memberRepository.findByEmail(email);
            model.addAttribute("member", member);
        }
    }
}