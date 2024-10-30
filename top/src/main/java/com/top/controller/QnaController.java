package com.top.controller;

import com.top.dto.NpageRequestDTO;
import com.top.dto.QnaDTO;
import com.top.entity.Member;
import com.top.repository.MemberRepository;
import com.top.service.QnaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/qna")
@Log4j2
@RequiredArgsConstructor
public class QnaController {
    private final QnaService service;
    private final MemberRepository memberRepository;

    @GetMapping("/")
    public String index(){

        return "redirect:/qna/list";
    }

    // List

    @GetMapping("/list")
    public void list(NpageRequestDTO npageRequestDTO, Model model){
        log.info("list............." + npageRequestDTO);
        model.addAttribute("result", service.getList(npageRequestDTO));
    }

    // Regist

    @GetMapping("/register")
    public void register(){
        log.info("regiser get...");
    }

    @PostMapping("/register")
    public String registerPost(QnaDTO dto, RedirectAttributes redirectAttributes) {
        // Log Info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName(); // getName

        Member member = memberRepository.findByEmail(currentUsername);
        dto.setMemberId(member.getId()); // 현재 사용자 ID 설정

        dto.setWriter(currentUsername); // Setting Name

        log.info("dto..." + dto);

        // New Entity Number
        Long qno = service.register(dto);
        redirectAttributes.addFlashAttribute("msg", qno);
        return "redirect:/qna/list";
    }

    // read & Update
    @GetMapping({"/read", "/modify"})
    public void read(long qno, @ModelAttribute("requestDTO") NpageRequestDTO requestDTO, Model model ){
        log.info("qno: " + qno);
        QnaDTO dto = service.read(qno);
        model.addAttribute("dto", dto);
    }

    // Update
    @PostMapping("/modify")
    public String modify(QnaDTO dto,
                         @ModelAttribute("requestDTO") NpageRequestDTO requestDTO,
                         RedirectAttributes redirectAttributes){
        log.info("post modify.........................................");
        log.info("dto: " + dto);
        service.modify(dto);
        redirectAttributes.addAttribute("page",requestDTO.getPage());
        redirectAttributes.addAttribute("type",requestDTO.getType());
        redirectAttributes.addAttribute("keyword",requestDTO.getKeyword());
        redirectAttributes.addAttribute("qno",dto.getQno());
        return "redirect:/qna/read";
    }

    // Delete
    @PostMapping("/remove")
    public String remove(long qno, RedirectAttributes redirectAttributes){
        log.info("qno: " + qno);
        service.remove(qno);
        redirectAttributes.addFlashAttribute("msg", qno);
        return "redirect:/qna/list";
    }
}

