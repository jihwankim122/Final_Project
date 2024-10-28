package com.top.dto;

import com.top.entity.Member;
import com.top.entity.QMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class NpageRequestDTO {
    private int page; // Page Number
    private int size; // List Size
    private String type;
    private String keyword;
    private Member member; // 추가된 부분

    public NpageRequestDTO(){
        this.page = 1;
        this.size = 10;
        
    }



    public Pageable getPageable(Sort sort){
        return PageRequest.of(page -1, size, sort);
    }
}
