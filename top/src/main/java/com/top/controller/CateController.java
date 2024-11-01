package com.top.controller;

import com.top.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.top.constant.ItemSellStatus;
import com.top.dto.ItemSearchDto;
import com.top.dto.MainItemDto;
import com.top.service.ItemServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CateController extends MemberBasicController {

    private final ItemServiceImpl itemService;

    @GetMapping(value = "/category/computer")
    public String Computer(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){


        // 1018 은열 수정
        // 기본적으로 SELL과 SOLD_OUT 상태로 필터링할 수 있도록 설정
        if (itemSearchDto.getSearchSellStatus() == null) {
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SELL);// 기본값 설정
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SOLD_OUT);// 기본값 설정

        }
        //241022 은열 추가
        //카테고리 번호 입력
        itemSearchDto.setCategory(0L);

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items = itemService.getCateItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "/category/computer";
    }

    @GetMapping(value = "/category/television")
    public String Television(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){


        // 1018 은열 수정
        // 기본적으로 SELL과 SOLD_OUT 상태로 필터링할 수 있도록 설정
        if (itemSearchDto.getSearchSellStatus() == null) {
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SELL);// 기본값 설정
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SOLD_OUT);// 기본값 설정

        }
        //241022 은열 추가
        //카테고리 번호 입력
        itemSearchDto.setCategory(1L);

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items = itemService.getCateItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "/category/television";
    }

    @GetMapping(value = "/category/refrigerator")
    public String Refrigerator(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){


        // 1018 은열 수정
        // 기본적으로 SELL과 SOLD_OUT 상태로 필터링할 수 있도록 설정
        if (itemSearchDto.getSearchSellStatus() == null) {
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SELL);// 기본값 설정
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SOLD_OUT);// 기본값 설정

        }
        //241022 은열 추가
        //카테고리 번호 입력
        itemSearchDto.setCategory(2L);

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items = itemService.getCateItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "/category/refrigerator";
    }
    @GetMapping(value = "/category/circulation")
    public String Circulation(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){


        // 1018 은열 수정
        // 기본적으로 SELL과 SOLD_OUT 상태로 필터링할 수 있도록 설정
        if (itemSearchDto.getSearchSellStatus() == null) {
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SELL);// 기본값 설정
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SOLD_OUT);// 기본값 설정

        }
        //241022 은열 추가
        //카테고리 번호 입력
        itemSearchDto.setCategory(3L);

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items = itemService.getCateItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "/category/circulation";
    }

}