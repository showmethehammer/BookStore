package com.example.bookstore.Book.exception;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageOrCountDto {
    private long count;
    private int pageSize;
}
