package com.top.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MemberFormDto {

    @NotEmpty(message = "이름을 입력하세요")
    private String name;

    @NotEmpty(message = "이메일을 입력하세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotEmpty(message = "비밀번호를 입력하세요")
    @Length(min=8, max=16, message="비밀번호는 8자 이상, 16자 이하로 입력해주세요")
    private String password;

    @NotEmpty(message = "주소를 입력하세요")
    private String address;

    @NotEmpty(message = "우편번호를 입력하세요")
    private String postcode;

    // 1025 성아 추가
    @NotEmpty(message = "상세 주소를 입력하세요")
    private String detailAddress; // 상세 주소

    //1028 유진 추가
    @NotEmpty(message = "전화번호를 입력하세요")
    @Pattern(regexp = "^(010|011|016|017|018|019)-\\d{3,4}-\\d{4}$",
            message = "전화번호는 010-1234-5678 형식으로 입력해주세요")
    private String phone;  // 전화번호 필드 추가

}
