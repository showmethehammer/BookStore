package com.example.bookstore.login.Dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IdCheckDto {
    @NotBlank(message = "아이디는 필수 항목입니다.")
    private String userName;
}
