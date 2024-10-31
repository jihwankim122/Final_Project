package com.top.service;

import com.top.constant.Grade;
import com.top.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private double discountBronze = 0.01; // 브론즈등급 고객 할인할 금액 1퍼센트
    private double discountSilver = 0.03; // 실버등급 고객 할인할 금액 3퍼센트
    private double discountGold = 0.05; // 골드등급 고객 할인할 금액 5퍼센트
    private double discountPlatinum = 0.1; // 플래티넘등급 고객 할인할 금액 10퍼센트



    @Override
    public int discount(Member member, int price) {
        double discountRate;
        if (member.getGrade() == Grade.BRONZE) {
            discountRate = discountBronze;
        } else if (member.getGrade() == Grade.SILVER) {
            discountRate = discountSilver;
        } else if (member.getGrade() == Grade.GOLD) {
            discountRate = discountGold;
        } else if (member.getGrade() == Grade.PLATINUM) {
            discountRate = discountPlatinum;
        } else {
            return 0;
        } return (int) (price * discountRate); // 비율에따라 할인금액 계산
    }
}
