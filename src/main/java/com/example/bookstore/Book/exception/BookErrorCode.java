package com.example.bookstore.Book.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookErrorCode {
    BOOK_NOT_FOUND_ERROR("책을 찾을수 없습니다."),
    BOOK_NOT_COUNT_MINUS_ERROR("책의 수량아 음수일 수 없습니다."),
    BOOK_NOT_SALE_NUMBER_ERROR("SALE 값을 잘못 입력하였습니다.");
    private final String description;
}
