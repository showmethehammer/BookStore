package com.example.bookstore.Book.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookErrorCode {
    BOOK_NOT_FOUND_ERROR("책을 찾을수 없습니다.");
    private final String description;
}
