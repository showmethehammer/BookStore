package com.example.bookstore.Book.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentException extends RuntimeException{
    private CommentErrorCode ErrorCode;
    private String errorMessage;
    public CommentException(CommentErrorCode code){
        this.ErrorCode = code;
        this.errorMessage = code.getDescription();
    }
}
