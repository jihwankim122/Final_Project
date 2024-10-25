package com.top.entity;


import com.top.constant.Role;
import com.top.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
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

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setPostcode(memberFormDto.getPostcode()); // 1025 성아 추가
        member.setAddress(memberFormDto.getAddress());
        member.setDetailAddress(memberFormDto.getDetailAddress()); // 1025 성아 추가
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.USER);
        return member;
    }

}
