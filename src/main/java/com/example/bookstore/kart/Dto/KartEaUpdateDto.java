package com.example.bookstore.kart.Dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KartEaUpdateDto {
    @NotNull(message = "ID는 필수 항목 입니다.")
    private String userId;              // userId
    @NotNull(message = "국제 일년번호는 필수 항목 입니다.")
    private String isbn;                // 국제 일년번호
    @NotNull(message = "수량 입력은 필수 항목 입니다.")
    private String ea;                  // 구매 수량
}
