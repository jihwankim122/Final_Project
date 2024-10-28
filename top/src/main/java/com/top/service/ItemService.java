package com.top.service;

import com.top.dto.ItemFormDto;
import com.top.dto.ItemSearchDto;
import com.top.dto.MainItemDto;
import com.top.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception;
    ItemFormDto getItemDtl(Long itemId);
    Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception;
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    Page<MainItemDto> getCateItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    void deleteItem(Long itemId) throws Exception; // 상품 삭제
}
