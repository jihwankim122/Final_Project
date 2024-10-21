package com.top.service;

import com.top.entity.ItemImg;
import org.springframework.web.multipart.MultipartFile;

public interface ItemImgService {
    void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception;
    void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception;
    void deleteItemImg(Long itemImgId) throws Exception; // 상품 삭제
}
