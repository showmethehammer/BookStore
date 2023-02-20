package com.example.bookstore.login.Dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class IdSearch {
    @NotBlank(message = "이름은 필수 항목 입니다.")
    private String name;
    @Email(message = "Email 형식에 맞게 입력하세요.")
    @NotBlank(message = "Email은 필수 항목 입니다.")
    private String email;
    @NotBlank(message = "전화번호는 필수 항목 입니다.")
    private String phone;
}
