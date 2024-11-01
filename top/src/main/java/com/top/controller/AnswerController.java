package com.top.controller;

import com.top.dto.AnswerDTO;
import com.top.entity.Member;
import com.top.service.AnswerService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/answers") // 기본 URL 매핑
@RequiredArgsConstructor
public class AnswerController extends MemberBasicController{

    private final AnswerService answerService;

    // 답변 등록
    @PostMapping
    public String register(@ModelAttribute AnswerDTO dto, RedirectAttributes redirectAttributes) {
        // log
        System.out.println("dto value ++++" + dto);

        if (dto.getQid() == null || dto.getMid() == null) {
            throw new IllegalArgumentException("QID and Member ID must not be null.");
        }

        Long id = answerService.register(dto);

        // 리다이렉트할 URL 설정
        redirectAttributes.addAttribute("qno", dto.getQid()); // Qna ID 추가
        return "redirect:/qna/read"; // 리다이렉트
    }



    @PostMapping("/delete")
    public String remove(@RequestParam Long id, @RequestParam Long qid, RedirectAttributes redirectAttributes) {
        answerService.remove(id);
        redirectAttributes.addAttribute("qno", qid);
        return "redirect:/qna/read"; // Redirect to the Qna page
        }

    }
