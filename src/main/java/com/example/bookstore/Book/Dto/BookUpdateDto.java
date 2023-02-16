package com.example.bookstore.Book.Dto;

import com.sun.istack.NotNull;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookUpdateDto {
    @NotBlank(message = "국제표준 도서번호는 필수 항목입니다.")
    private String isbn;                            // 국제 표준 도서 번호
    @NotBlank(message = "도서 수량은 필수 항목 입니다.")
    private String statusEa;                        // 제고량
    @NotBlank(message = "할인율은 필수 항목입니다.")
    @NotNull
    private String sale;                           // 할인가
}
