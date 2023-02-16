package com.example.bookstore.kart.excption;


import com.example.bookstore.login.exception.UserErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KartErrorException extends RuntimeException {
    private KartErrorCode ErrorCode;
    private String errorMessage;
    public KartErrorException(KartErrorCode code){
        this.ErrorCode = code;
        this.errorMessage = code.getDescription();
    }

}
