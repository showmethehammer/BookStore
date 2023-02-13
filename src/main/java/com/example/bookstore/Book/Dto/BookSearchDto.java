package com.example.bookstore.Book.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchDto {

    private Integer bookType;
    @NotBlank(message = "검색어를 입력하세요.")
    private String isbn;

}
