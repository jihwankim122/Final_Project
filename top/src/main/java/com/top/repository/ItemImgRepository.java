package com.top.repository;

import com.top.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
    List<ItemImg> findByItemNoOrderByIdAsc(Long itemId); // 1101 성아 수정

    ItemImg findByItemNoAndRepimgYn(Long itemId, String repimgYn); // 1101 성아 수정


}
