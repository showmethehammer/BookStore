package com.example.bookstore.Book.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookException extends RuntimeException {
    private BookErrorCode ErrorCode;
    private String errorMessage;
    public BookException(BookErrorCode code){
        this.ErrorCode = code;
        this.errorMessage = code.getDescription();
    }

}
