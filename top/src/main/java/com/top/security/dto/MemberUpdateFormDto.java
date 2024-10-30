package com.top.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MemberUpdateFormDto {

    @NotEmpty(message = "이름을 입력하세요")
    private String name;

    @NotEmpty(message = "아이디를 입력하세요")
    private String username;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @Length(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요")
    private String password;

    private String confirmPassword;

    @NotEmpty(message = "주소를 입력하세요")
    private String address;

    @NotEmpty(message = "우편번호를 입력하세요")
    private String postcode;  // 우편번호 추가

    @NotEmpty(message = "상세 주소를 입력하세요")
    private String detailAddress;  // 상세 주소 추가

    @Pattern(regexp = "^(010|011|016|017|018|019)-\\d{3,4}-\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
    @NotEmpty(message = "전화번호를 입력하세요")
    private String phone;  // 1028 유진 추가: 전화번호 필드 및 유효성 검사
}
