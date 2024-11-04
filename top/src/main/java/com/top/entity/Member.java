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
    private Grade grade;

    // 1024 유진 추가: 생성자 및 수정자 정보
    private String createdBy; // 생성자
    private String modifiedBy; // 수정자
    private String nickname; // 닉네임

    // 1028 유진 추가: 전화번호 필드
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    //241101 은열 추가

    private double totalSpentAmount;//누적 주문금액

    public void addOrderPrice(int finalPrice) {
        this.totalSpentAmount += finalPrice;
        updateRank();

    }
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

    // 1028 유진 수정 추가 - Member 생성 메서드 개선
    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setPostcode(memberFormDto.getPostcode()); // 1025 성아 추가
        member.setAddress(memberFormDto.getAddress());
        member.setDetailAddress(memberFormDto.getDetailAddress()); // 1025 성아 추가
        member.setPhone(memberFormDto.getPhone()); // 1028 유진 추가: 전화번호 설정
        String password = passwordEncoder.encode(memberFormDto.getPassword());
         //241023 은열 추가
         member.setGrade(Grade.BRONZE);
        member.setPassword(password);
        member.setRole(Role.ADMIN);
        return member;
    }

    // 1028 유진 수정 추가 - 비밀번호 업데이트 메서드
    public void updatePassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
    }

    // 1028 유진 수정 추가 - 회원 정보 업데이트 메서드
    public void updateMemberInfo(
            String email, String address, String postcode, String detailAddress, String phone) {
        this.email = email;
        this.address = address;
        this.postcode = postcode;
        this.detailAddress = detailAddress;
        this.phone = phone;
    }

    @Builder
    public Member(Long id, String name, String email, String password, String address, String postcode, String detailAddress, Role role, String nickname, String phone) {
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
    }
}
