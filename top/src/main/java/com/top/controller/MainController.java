package com.top.controller;

import com.top.entity.Member;
import com.top.service.MemberService;
import com.top.service.MemberServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.top.constant.ItemSellStatus;
import com.top.dto.ItemSearchDto;
import com.top.dto.MainItemDto;
import com.top.service.ItemServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController extends MemberBasicController {

    private final ItemServiceImpl itemService;
    private final MemberServiceImpl memberService; // 1031 성아 추가


    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){

        // 1018 은열 수정
        // 기본적으로 SELL과 SOLD_OUT 상태로 필터링할 수 있도록 설정
        if (itemSearchDto.getSearchSellStatus() == null) {
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SELL);// 기본값 설정
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SOLD_OUT);// 기본값 설정

        }

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        // 1031 성아 추가
        // 로그인한 사용자 정보 가져오기(챗봇)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 이메일 가져오기
        try {
            Member member = memberService.findByEmail(email); // 이메일로 회원 조회
            model.addAttribute("memberName", member.getName()); // 로그인 상태 -> 이름 가져옴
        } catch (UsernameNotFoundException e) {
            model.addAttribute("memberName", "Guest"); // 로그아웃 상태 -> Guest로 출력
        }


        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        // model.addAttribute("maxPage", 5);
        model.addAttribute("page", page.isPresent() ? page.get() : 0); // 현재 페이지 // 1107 성아 추가

        return "main";
    }

    // 1107 성아 추가
    @GetMapping("/load-items")
    @ResponseBody
    public List<MainItemDto> loadItems(@RequestParam int page, ItemSearchDto itemSearchDto) {
        Pageable pageable = PageRequest.of(page, 6);  // 페이지당 6개씩 불러옴
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
        return items.getContent();  // 실제 데이터만 반환
    }

}