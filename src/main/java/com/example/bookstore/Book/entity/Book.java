package com.example.bookstore.Book.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;                                // 등록 ID
    @NotNull
    private String title;                           // 제목
    @NotNull
    private String contents;                        // 도서 소개
    @NotNull
    private String url;                             // 도서 상세 URL
    @NotNull
    private String isbn;                            // 국제 표준 도서 번호
    @NotNull
    private String datetime;                 // 출판일자
    @NotNull
    private String authors;                   // 저자 리스트
    @NotNull
    private String publisher;                       // 출판사
    @NotNull
    private String translators;               // 번역자 리스트
    @NotNull
    private Integer price;                          // 도서 정가
    @NotNull
    private Integer sale_price;                     // 도서 판매가
    @NotNull
    private String thumbnail;                       // 도서 표지 미리보기 URL
    private String status;                          // 판매 상태
    private Integer statusEa;                        // 제고량
    private Integer sale;                           // 할인가
}
