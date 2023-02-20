package com.example.bookstore.login.Dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordSearch {
    @NotBlank(message = "ID는 필수 항목 입니다.")
    private String userName;
    @NotBlank(message = "이름은 필수 항목 입니다.")
    private String name;
    @NotBlank(message = "전화번호는 필수 항목 입니다.")
    private String phone;
}
