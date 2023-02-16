package com.example.bookstore.kart.Dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KartDeleteDto {
    @NotNull(message = "ID는 필수 항목 입니다.")
    private String username;              // userId
    @NotNull(message = "국제 일년번호는 필수 항목 입니다.")
    private String isbn;                // 국제 일년번호
}
