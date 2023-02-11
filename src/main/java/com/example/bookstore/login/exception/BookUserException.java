package com.example.bookstore.login.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookUserException extends RuntimeException {
    private UserErrorCode ErrorCode;
    private String errorMessage;
    public BookUserException(UserErrorCode code){
        this.ErrorCode = code;
        this.errorMessage = code.getDescription();
    }

}
