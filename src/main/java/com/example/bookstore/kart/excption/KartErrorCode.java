package com.example.bookstore.kart.excption;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KartErrorCode {
    KART_NOT_FOUND_ERROR("등록되지 않은 상품입니다."),
    KART_SCARCE_BOOK_ERROR("수량이 부족합니다."),
    KART_NOT_SALE_COUNT_ERROR("세일항목을 잘못 입력하셨습니다.");
    private final String description;
}
