package com.example.bookstore.kart.excption;

import com.example.bookstore.login.exception.UserErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KartException {
    private KartErrorCode code;
    private String message;
}
