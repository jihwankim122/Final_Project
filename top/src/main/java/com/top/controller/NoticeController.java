package com.top.controller;

import com.top.dto.NoticeDTO;
import com.top.dto.NpageRequestDTO;
import com.top.entity.Member;
import com.top.repository.MemberRepository;
import com.top.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

import static com.top.entity.QMember.member;

@Controller
@RequestMapping("/notice")
@Log4j2
@RequiredArgsConstructor
public class NoticeController extends MemberBasicController {
    private final NoticeService service;
    private final MemberRepository memberRepository;
    private final NoticeService noticeService;

    @GetMapping("/")
    public String index(){

        return "redirect:/notice/list";
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
    public String registerPost(NoticeDTO dto, RedirectAttributes redirectAttributes) {
        // Log Info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName(); // getName

        Member member = memberRepository.findByEmail(currentUsername);
        dto.setMemberId(member.getId()); // 현재 사용자 ID 설정

        dto.setWriter(currentUsername); // Setting Name


        // New Entity Number
        Long nno = service.register(dto);
        redirectAttributes.addFlashAttribute("msg", nno);
        return "redirect:/notice/list";
    }

    // read & Update
    @GetMapping({"/read", "/modify"})
    public void read(long nno, @ModelAttribute("requestDTO") NpageRequestDTO requestDTO, Model model ){
        log.info("nno: " + nno);
        NoticeDTO dto = service.read(nno);
        model.addAttribute("dto", dto);
    }

    // Update
    @PostMapping("/modify")
    public String modify(NoticeDTO dto,
                         @ModelAttribute("requestDTO") NpageRequestDTO requestDTO,
                         RedirectAttributes redirectAttributes){
        log.info("post modify.........................................");
        log.info("dto: " + dto);
        service.modify(dto);
        redirectAttributes.addAttribute("page",requestDTO.getPage());
        redirectAttributes.addAttribute("type",requestDTO.getType());
        redirectAttributes.addAttribute("keyword",requestDTO.getKeyword());
        redirectAttributes.addAttribute("nno",dto.getNno());
        return "redirect:/notice/read";
    }

    // Delete
    @PostMapping("/remove")
    public String remove(long nno, RedirectAttributes redirectAttributes){
        log.info("nno: " + nno);
        service.remove(nno);
        redirectAttributes.addFlashAttribute("msg", nno);
        return "redirect:/notice/list";
    }

    // Pinned
    @PostMapping("/togglePinned/{nno}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> togglePinned(@PathVariable Long nno) {
        Map<String, Object> response = new HashMap<>();
        try {
            service.togglePinned(nno);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}

