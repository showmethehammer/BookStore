package com.example.bookstore.kart.Dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KartAddDto {
    @NotNull(message = "아이디는 필수 항목입니다.")
    private String username;      // userId
    @NotNull(message = "일년번호는 필수 항목입니다.")
    private String isbn;        // 국제 일년번호
    @NotNull(message = "수량입력은 필수 항목입니다.")
    private String ea;              // 수량
    @NotNull(message = "세일율은 필수 항목 입니다.")
    private String sale;

}
