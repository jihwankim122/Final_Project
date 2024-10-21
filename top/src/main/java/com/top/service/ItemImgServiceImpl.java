package com.top.service;

import com.top.entity.ItemImg;
import com.top.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgServiceImpl implements ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;
    private final FileService fileService; // 인터페이스로 변경

    @Override
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }

        // 상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }

    @Override
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        if (!itemImgFile.isEmpty()) {
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            // 기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }

    @Override
    public void deleteItemImg(Long itemImgId) throws Exception {
        ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                .orElseThrow(EntityNotFoundException::new);

        // 기존 이미지 파일 삭제
        if (!StringUtils.isEmpty(savedItemImg.getImgName())) {
            fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
        }

        // 데이터베이스에서 이미지 정보 삭제
        itemImgRepository.delete(savedItemImg);
    }
}
