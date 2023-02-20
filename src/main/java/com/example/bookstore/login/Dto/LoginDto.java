package com.example.bookstore.login.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class LoginDto {
    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String userName;
    @NotBlank(message = "비밀번호는 필수 입력 항목 입니다.")
    private String Password;
}