package com.top.controller;

import com.top.entity.Member;
import com.top.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;
import com.top.dto.ItemFormDto;

import com.top.service.ItemService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import com.top.dto.ItemSearchDto;
import com.top.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController extends MemberBasicController {

    private final ItemService itemService;
    private final MemberService memberService;

    // 메인 상품 상세보기
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);
        model.addAttribute("member", member); // 1101 성아 추가

        // 디버깅 로그 추가
        System.out.println("리뷰 평균: " + itemFormDto.getAvg());
        System.out.println("리뷰 개수: " + itemFormDto.getReviewCnt());

        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }

    // 상품 등록 페이지
    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemCreate";  // itemCreate.html로 이동
    }

    // 상품 등록 처리
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        if (bindingResult.hasErrors()) {
            return "item/itemCreate";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getNo() == null) { // 1101 성아 getId -> getNo 수정
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemCreate";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemCreate";
        }

        return "redirect:/admin/items";
    }

    // 관리자 상품 상세보기 페이지
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemRead(@PathVariable("itemId") Long itemId, Model model){
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            return "redirect:/admin/items";
        }

        return "item/itemRead";  // itemRead.html로 이동
    }

    // 상품 수정 페이지
    @GetMapping(value = "/admin/item/edit/{itemId}")
    public String itemEdit(@PathVariable("itemId") Long itemId, Model model){
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            return "redirect:/admin/items";
        }

        return "item/itemEdit";  // itemEdit.html로 이동
    }


    // 상품 수정 처리
    @PostMapping(value = "/admin/item/edit/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model) {

        if (bindingResult.hasErrors()) {
            return "item/itemEdit";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemEdit";
        }

        return "redirect:/admin/items";
    }


    // 상품 삭제 처리
    @PostMapping(value = "/admin/item/delete/{itemId}")
    public String itemDelete(@PathVariable("itemId") Long itemId, Model model) throws Exception {
        try {
            itemService.deleteItem(itemId);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
        }

        return "redirect:/admin/items";
    }

    // 상품 관리 페이지
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        Pageable pageable = PageRequest.of(page.orElse(0), 3);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }
}
