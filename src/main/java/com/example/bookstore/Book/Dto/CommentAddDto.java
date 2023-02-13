package com.example.bookstore.Book.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentAddDto {
    @NotBlank(message = "isbn은 필수 항목입니다.")
    private String isbn;
    @NotBlank(message = "이름은 필수 항목입니다.")
    private String userName;
    private String text;
}