package com.top.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Data
public class NpageResultDTO<DTO, EN> {

    private List<DTO> dtoList; // DTO list
    private int totalPage; // Total Page Number
    private int page; // Page Number
    private int size; // Page Size
    private int start, end; // Start&End Page Number
    private boolean prev, next;
    private List<Integer> pageList; // Page Number List

    public NpageResultDTO(Page<EN> result, Function<EN,DTO> fn){
        dtoList = result.stream().map(fn).collect(Collectors.toList()); // fn은 람다식 함수를 가리키는 레퍼런스
        totalPage = result.getTotalPages();
        makePageList(result.getPageable());
    }

    private void makePageList(Pageable pageable){
        this.page = pageable.getPageNumber() + 1; // Start From 0
        this.size = pageable.getPageSize();
        //temp end page
        int tempEnd = (int)(Math.ceil(page/10.0)) * 10;
        start = tempEnd - 9;
        prev = start > 1;
        end = totalPage > tempEnd ? tempEnd: totalPage;
        next = totalPage > tempEnd;
        pageList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
    }
}

