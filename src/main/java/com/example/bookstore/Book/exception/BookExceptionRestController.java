package com.example.bookstore.Book.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BookExceptionRestController {
    @ExceptionHandler(BookException.class)
    public BookErrorException bookException(BookException e){
        return new BookErrorException(e.getErrorCode(), e.getMessage());
    }
    @ExceptionHandler(CommentException.class)
    public CommentErrorException commentException(CommentException e){
        return new CommentErrorException(e.getErrorCode(), e.getMessage());
    }
}
