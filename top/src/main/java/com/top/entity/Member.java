package com.top.entity;


import com.top.constant.Role;
import com.top.dto.MemberFormDto;
import lombok.*;

import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Member extends BaseEntity{

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    private String postcode; // 1025 성아 추가 // 우편번호
    private String detailAddress; // 1025 성아 추가 // 상세주소


    //1024 유진 추가
    private String createdBy;
    private String modifiedBy;
    private String nickname;
    //1024 유진 끝

    @Builder
    public Member(Long id, String name, String email, String password, String address, String postcode, String detailAddress, Role role, String nickname) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.postcode = postcode;
        this.detailAddress = detailAddress;
        this.role = role;
        this.nickname = nickname;
    }

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .name(memberFormDto.getName())
                .email(memberFormDto.getEmail())
                .password(passwordEncoder.encode(memberFormDto.getPassword()))
                .address(memberFormDto.getAddress())
                .postcode(memberFormDto.getPostcode())
                .detailAddress(memberFormDto.getDetailAddress())
                .role(Role.USER)
                .build();
    }

}
