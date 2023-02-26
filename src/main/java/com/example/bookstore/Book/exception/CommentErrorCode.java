package com.example.bookstore.Book.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentErrorCode {
    COMMENT_NOT_FOUND_ERROR("댓글이 없습니다."),
    COMMENT_NOT_MATCH_ID_ERROR("댓글과 아이디 불일치합니다.");
    private final String description;
}
