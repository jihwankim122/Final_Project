package com.top.entity;

import com.top.constant.Role;
import com.top.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
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

    // 1024 유진 추가: 생성자 및 수정자 정보
    private String createdBy; // 생성자
    private String modifiedBy; // 수정자
    private String nickname; // 닉네임

    // 1028 유진 추가: 전화번호 필드
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

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
        member.setPassword(password);
        member.setRole(Role.USER);
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
}
