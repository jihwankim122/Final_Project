package com.top.controller;

import com.top.dto.AnswerDTO;
import com.top.dto.NpageRequestDTO;
import com.top.dto.NpageResultDTO;
import com.top.dto.QnaDTO;
import com.top.entity.Member;
import com.top.entity.Qna;
import com.top.repository.MemberRepository;
import com.top.repository.QnaRepository;
import com.top.service.AnswerService;
import com.top.service.QnaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/qna")
@Log4j2
@RequiredArgsConstructor
public class QnaController extends MemberBasicController  {
    private final QnaService service;
    private final MemberRepository memberRepository;
    private final AnswerService answerService;


    @GetMapping("/")
    public String index(){

        return "redirect:/qna/list";
    }

    // List

    @GetMapping("/list")
    public void list(NpageRequestDTO npageRequestDTO, Model model) {
        log.info("list............." + npageRequestDTO);

        // Get the list from service
        NpageResultDTO<QnaDTO, Qna> result = service.getList(npageRequestDTO);


        // Add the result to the model
        model.addAttribute("result", result);

        // Check if the user is an admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
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
        dto.setMemberId(member.getId()); // Current User Id

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

        // Qna 엔티티 조회
        Qna qnaEntity = service.getQnaById(qno); // ID로 Qna 조회

        // 댓글 목록 추가
        List<AnswerDTO> answers = answerService.getList(qnaEntity); // Qna 엔티티를 전달
        model.addAttribute("answers", answers); // 모델에 추가


        // 현재 로그인한 사용자의 ID 추가
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Member member = memberRepository.findByEmail(currentUsername);
        model.addAttribute("memberId", member.getId()); // Add to Model
        model.addAttribute("currentUsername", currentUsername); // 현재 사용자 이름 추가

        // Check if the user is an admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

    }

    // Update

    @PostMapping("/modify")
    public String modify(QnaDTO dto,
                         @ModelAttribute("requestDTO") NpageRequestDTO requestDTO,
                         RedirectAttributes redirectAttributes){
        log.info("post modify.........................................");
        log.info("dto: " + dto);

        // 현재 로그인한 사용자의 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // 작성자 확인
        Qna existingQna = service.getQnaById(dto.getQno());
        if (!existingQna.getWriter().equals(currentUsername)) {
            throw new RuntimeException("You do not have permission to modify this QnA.");
        }

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

        // 현재 로그인한 사용자의 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // 작성자 확인
        Qna existingQna = service.getQnaById(qno);
        if (!existingQna.getWriter().equals(currentUsername)) {
            throw new RuntimeException("You do not have permission to delete this QnA.");
        }

        service.remove(qno);
        redirectAttributes.addFlashAttribute("msg", qno);
        return "redirect:/qna/list";
    }
}

