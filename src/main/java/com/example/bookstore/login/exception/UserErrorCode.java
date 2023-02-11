package com.example.bookstore.login.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode {
    USER_NOT_FOUND_ERROR("ID와 비밀번호가 일치하지 않습니다."),
    USER_NOT_AUTH_ERROR("인증되지 않은 ID 입니다."),
    USER_NOT_DATA_ERROR("정보가 일치하지 않습니다."),
    USER_AUTH_TIME_OVER_ERROR("인증시간이 초과하였습니다 메일 재송부하였으니 다시인증하세요."),
    USER_ON_AUTH_ERROR("인증된 ID 입니다."),
    USER_ID_EXIST_ERROR("중복된 ID가 있습니다."),
    USER_ID_EMAIL_FOUND_ERROR("ID와 Email이 일치 하지 않습니다."),
    USER_NOT_TOKEN_NUMBER("Token이 유효하지 않습니다.");

    private final String description;
}
