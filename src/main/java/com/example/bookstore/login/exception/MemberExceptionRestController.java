package com.example.bookstore.login.exception;


import com.example.bookstore.login.exception.BookUserException;
import com.example.bookstore.login.exception.MemberException;
import com.example.bookstore.login.exception.ResponseError;
import com.example.bookstore.login.exception.ValidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MemberExceptionRestController {
    @ExceptionHandler(BookUserException.class)
    public MemberException bookUserIdAuthExceptionHeader(BookUserException e){
        return new MemberException(e.getErrorCode(), e.getErrorMessage());
    }
    @ExceptionHandler(ValidException.class)
    public ResponseError validException(ValidException e){
        return new ResponseError(e.getCode(),e.getMessage());
    }
}
