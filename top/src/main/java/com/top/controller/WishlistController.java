package com.top.controller;

import com.top.dto.WishlistDetailDto;
import com.top.dto.WishlistItemDto;
import com.top.service.WishlistServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistServiceImpl wishlistService;


    @PostMapping(value = "/wishlist")
    public @ResponseBody ResponseEntity<?> addWishlistItem(
            @RequestBody @Valid WishlistItemDto wishlistItemDto,
            BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();

        
        boolean isAlreadyWished = wishlistService.isItemAlreadyWished(wishlistItemDto.getItemId(), email);
        if (isAlreadyWished) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }


        Long wishlistItemId;
        try {
            wishlistItemId = wishlistService.addWishlist(wishlistItemDto, email);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(wishlistItemId, HttpStatus.OK);
    }



    @GetMapping(value = "/wishlist")
    public String viewWishlist(Principal principal, Model model) {
        List<WishlistDetailDto> wishlistDetailList = wishlistService.getWishlist(principal.getName());
        model.addAttribute("wishlistItems", wishlistDetailList);
        return "wishlist/wishlist"; // 위시리스트 목록을 보여주는 뷰 이름
    }


    @DeleteMapping(value = "/wishlistItem/{wishlistItemId}")
    public @ResponseBody ResponseEntity<?> deleteWishlistItem(@PathVariable("wishlistItemId") Long wishlistItemId, Principal principal) {
        try {
            if (!wishlistService.validateWishlistItem(wishlistItemId, principal.getName())) {
                return new ResponseEntity<String>("권한이 없습니다.", HttpStatus.FORBIDDEN);
            }

            wishlistService.deleteWishlistItem(wishlistItemId);
            return new ResponseEntity<Long>(wishlistItemId, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    


//    @DeleteMapping(value = "/wishlistItemByItem/{itemId}")
//    public @ResponseBody ResponseEntity deleteWishlistItemByItemId(@PathVariable("itemId") Long itemId, Principal principal) {
//        try {
//            wishlistService.deleteWishlistItemByItemId(itemId, principal.getName());
//            return new ResponseEntity<Long>(itemId, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }

    // 06Nov 의빈추가
    @PostMapping(value = "/wishlistItem/{wishlistItemId}/moveToCart")
    public @ResponseBody ResponseEntity<?> moveItemToCart(@PathVariable("wishlistItemId") Long wishlistItemId, Principal principal) {
        try {
            String email = principal.getName();
            Long cartItemId = wishlistService.moveItemToCart(wishlistItemId, email);
            return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
