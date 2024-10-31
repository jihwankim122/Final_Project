package com.top.service;

import com.top.entity.Member;

public interface DiscountService {

    int discount(Member member, int price);
}
