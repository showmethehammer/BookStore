package com.example.bookstore.Book.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateDto {
    @NotBlank(message = "입력오류")
    private String id;
    @NotBlank(message = "isbn은 필수 항목입니다.")
    private String isbn;
    private String text;
    @NotBlank(message = "이름은 필수 항목입니다.")
    private String userName;

    @NotBlank(message = "비밀번호 미입력")
    private String password;

}