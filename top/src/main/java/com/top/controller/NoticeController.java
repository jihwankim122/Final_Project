package com.top.controller;

import com.top.dto.NoticeDTO;
import com.top.dto.NpageRequestDTO;
import com.top.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notice")
@Log4j2
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService service;

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
    public String registerPost(NoticeDTO dto, RedirectAttributes redirectAttributes){
        log.info("dto..." + dto);
        //새로 추가된 엔티티의 번호
        Long gno = service.register(dto);
        redirectAttributes.addFlashAttribute("msg", gno);
        return "redirect:/notice/list";
    }

    // read & Update
    @GetMapping({"/read", "/modify"})
    public void read(long gno, @ModelAttribute("requestDTO") NpageRequestDTO requestDTO, Model model ){
        log.info("gno: " + gno);
        NoticeDTO dto = service.read(gno);
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
        redirectAttributes.addAttribute("gno",dto.getGno());
        return "redirect:/notice/read";
    }

    // Delete
    @PostMapping("/remove")
    public String remove(long gno, RedirectAttributes redirectAttributes){
        log.info("gno: " + gno);
        service.remove(gno);
        redirectAttributes.addFlashAttribute("msg", gno);
        return "redirect:/notice/list";
    }
}
