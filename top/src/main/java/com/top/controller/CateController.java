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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CateController extends MemberBasicController {

    private final ItemServiceImpl itemService;

    // 공통 로직을 처리하는 메서드
    private String getCategoryItems(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model, Long categoryId, String viewName) {
        // 기본 값 설정
        if (itemSearchDto.getSearchSellStatus() == null) {
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SELL);  // 판매중 아이템
            itemSearchDto.setSearchSellStatus(ItemSellStatus.SOLD_OUT);  // 품절 아이템
        }

        // 카테고리 설정
        itemSearchDto.setCategory(categoryId);

        // 페이지네이션 처리
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 8);
        Page<MainItemDto> items = itemService.getCateItemPage(itemSearchDto, pageable);

        // 모델에 데이터 추가
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("page", page.isPresent() ? page.get() : 0);  // 무한스크롤용 현재 페이지

        // 카테고리 페이지 뷰 반환
        return viewName;
    }

    // 카테고리별 메서드들
    @GetMapping(value = "/category/computer")
    public String Computer(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        return getCategoryItems(itemSearchDto, page, model, 0L, "/category/computer");
    }

    @GetMapping(value = "/category/television")
    public String Television(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        return getCategoryItems(itemSearchDto, page, model, 1L, "/category/television");
    }

    @GetMapping(value = "/category/refrigerator")
    public String Refrigerator(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        return getCategoryItems(itemSearchDto, page, model, 2L, "/category/refrigerator");
    }

    @GetMapping(value = "/category/circulation")
    public String Circulation(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        return getCategoryItems(itemSearchDto, page, model, 3L, "/category/circulation");
    }

    @GetMapping(value = "/category/washingMachine")
    public String WashingMachine(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        return getCategoryItems(itemSearchDto, page, model, 4L, "/category/washingMachine");
    }

    @GetMapping(value = "/category/clothesDryer")
    public String ClothesDryer(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        return getCategoryItems(itemSearchDto, page, model, 5L, "/category/clothesDryer");
    }

    @GetMapping(value = "/category/mobilePhone")
    public String MobilePhone(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        return getCategoryItems(itemSearchDto, page, model, 6L, "/category/mobilePhone");
    }

    @GetMapping(value = "/category/vacuum")
    public String Vacuum(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        return getCategoryItems(itemSearchDto, page, model, 7L, "/category/vacuum");
    }

    @GetMapping(value = "/category/dishwasher")
    public String Dishwasher(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        return getCategoryItems(itemSearchDto, page, model, 8L, "/category/dishwasher");
    }

    @GetMapping(value = "/category/induction")
    public String Induction(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        return getCategoryItems(itemSearchDto, page, model, 9L, "/category/induction");
    }

    // 무한 스크롤을 위한 API 엔드포인트
    @GetMapping("/load-items")
    @ResponseBody
    public List<MainItemDto> loadItems(@RequestParam int page, ItemSearchDto itemSearchDto, @RequestParam Long categoryId) {
        Pageable pageable = PageRequest.of(page, 8);  // 페이지당 8개씩 불러옴
        itemSearchDto.setCategory(categoryId);
        Page<MainItemDto> items = itemService.getCateItemPage(itemSearchDto, pageable);
        return items.getContent();  // 실제 데이터만 반환
    }
}
