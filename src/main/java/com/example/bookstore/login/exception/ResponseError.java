package com.example.bookstore.login.exception;

import lombok.*;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseError {
    private String field;
    private String message;

    public static ResponseError of(FieldError e) {
        return ResponseError.builder()
                .field(e.getField())
                .message(e.getDefaultMessage())
                .build();
    }

    public static void responseEntity(Errors errors) {
        List<ResponseError> responseErrors = new ArrayList<>();
        errors.getAllErrors().forEach((e) -> {
            responseErrors.add(ResponseError.of((FieldError) e));
        });
        throw new ValidException(responseErrors.get(0).field, responseErrors.get(0).message);
    }

}
