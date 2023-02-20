package com.example.bookstore.payment.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private String userName;            // userId
    @NotNull
    private String title;               // 제목
    @NotNull
    private String isbn;                // 국제 일년번호
    @NotNull
    private String thumbnail;           // 표지 URL
    @NotNull
    private Integer ea;                 // 구매 수량
}