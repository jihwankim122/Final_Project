package com.top.entity;

import com.top.constant.Grade;
import com.top.constant.Role;
import com.top.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    private String postcode; // 1025 성아 추가: 우편번호
    private String detailAddress; // 1025 성아 추가: 상세주소

    //241023 은열 추가
    @Enumerated(EnumType.STRING)
    private Grade grade; // 회원 등급 필드

    // 1024 유진 추가: 생성자 및 수정자 정보
    private String createdBy; // 생성자 정보
    private String modifiedBy; // 수정자 정보
    private String nickname; // 닉네임 필드
    private String phone; // 전화번호 필드

    @Enumerated(EnumType.STRING)
    private Role role; // 회원 권한 필드

    //241101 은열 추가
    private double totalSpentAmount; // 누적 주문 금액 필드

    private boolean isSocial; // 소셜 로그인 여부 필드

    // 주문 금액을 누적하고 회원의 등급을 업데이트하는 메서드
    public void addOrderPrice(int finalPrice) {
        this.totalSpentAmount += finalPrice;
        updateRank();
    }

    // 누적 주문 금액에 따라 회원의 등급을 업데이트하는 메서드
    public void updateRank() {
        if (totalSpentAmount >= 500000) {
            grade = Grade.PLATINUM;
        } else if (totalSpentAmount >= 300000) {
            grade = Grade.GOLD;
        } else if (totalSpentAmount >= 100000) {
            grade = Grade.SILVER;
        } else {
            grade = Grade.BRONZE;
        }
    }

    // 회원 생성 메서드 - MemberFormDto의 정보와 비밀번호 인코더를 사용해 새로운 회원 객체 생성
    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setPostcode(memberFormDto.getPostcode());
        member.setAddress(memberFormDto.getAddress());
        member.setDetailAddress(memberFormDto.getDetailAddress());
        member.setPhone(memberFormDto.getPhone());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setGrade(Grade.BRONZE); // 기본 등급 설정
        member.setRole(Role.ADMIN); // 기본 권한 설정
        member.setSocial(false); // 일반 회원으로 생성
        return member;
    }

    // 비밀번호를 업데이트하는 메서드
    public void updatePassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
    }

    // 회원 정보 업데이트 메서드
    public void updateMemberInfo(String email, String address, String postcode, String detailAddress, String phone) {
        this.email = email;
        this.address = address;
        this.postcode = postcode;
        this.detailAddress = detailAddress;
        this.phone = phone;
    }

    @Builder
    public Member(Long id, String name, String email, String password, String address, String postcode, String detailAddress, Role role, String nickname, String phone, boolean isSocial) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.postcode = postcode;
        this.detailAddress = detailAddress;
        this.role = role;
        this.nickname = nickname;
        this.phone = phone;
        this.isSocial = isSocial;
    }

    // 소셜 로그인 여부 확인 메서드
    public boolean isSocial() {
        return isSocial;
    }

    // 소셜 로그인 여부 설정 메서드
    public void setSocial(boolean social) {
        this.isSocial = social;
    }
}
