package com.example.bookstore.kart.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Kart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private String userId;              // userId
    @NotNull
    private String title;               // 제목
    @NotNull
    private String isbn;                // 국제 일년번호
    @NotNull
    private String thumbnail;           // 표지 URL
    @NotNull
    private Integer ea;                  // 구매 수량
    @NotNull
    private Integer price;                //  구매 가격
    private Integer sale_price;           //  할인 적용가
    @NotNull
    private Integer sale;                 // 할인율
    @NotNull
    private String authors;               // 저자
}
