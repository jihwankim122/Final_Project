package com.top.service;

import com.top.constant.ItemSellStatus;
import com.top.dto.ItemFormDto;
import com.top.entity.CartItem;
import com.top.entity.Item;
import com.top.entity.ItemImg;
import com.top.repository.CartItemRepository;
import com.top.repository.ItemImgRepository;
import com.top.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;

import com.top.dto.ItemImgDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.top.dto.ItemSearchDto;
import com.top.dto.MainItemDto;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;


    @Override
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        // 이미지 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if (i == 0)
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    @Override
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        // 이미지 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

    @Transactional
    @Override
    public void deleteItem(Long itemId) throws Exception {
        // 해당 아이템 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        //1017 은열추가 삭제상태로 변경
        item.setItemSellStatus((ItemSellStatus.DELETE));
//        // 아이템과 연관된 이미지 조회
//        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
//
//        // 아이템 이미지 삭제
//        for (ItemImg itemImg : itemImgList) {
//            itemImgService.deleteItemImg(itemImg.getId());
//        }
        // 장바구니 상품 조회
        System.out.println("Looking for cart items with itemId: " + itemId);
        List<CartItem> cartItems = cartItemRepository.findByItemId(itemId);
        System.out.println("Found cart items: " + cartItems.size());
        // 장바구니 상품 삭제
        if (!cartItems.isEmpty()) { // 장바구니 항목이 존재하는 경우에만 삭제
            cartService.deleteCartItemByItemId(itemId); // 아이템 ID를 사용하여 장바구니에서 삭제

        }

        itemRepository.save(item);
//        // 아이템 삭제
//        itemRepository.delete(item);
    }
}
