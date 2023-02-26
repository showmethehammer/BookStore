package com.example.bookstore.kart.excption;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class KartExceptionRestController {
    @ExceptionHandler(KartErrorException.class)
    public KartException bookUserIdAuthExceptionHeader(KartErrorException e){
        return new KartException(e.getErrorCode(), e.getErrorMessage());
    }

}
