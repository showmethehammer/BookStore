package com.example.bookstore.kart.controller;

import com.example.bookstore.kart.Dto.*;
import com.example.bookstore.kart.service.KartService;
import com.example.bookstore.login.exception.ValidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KartController {
    private final KartService kartService;
    @PostMapping("/api/kart/add")
    public ResponseEntity<?> kartAdd(@RequestBody @Valid KartAddDto kartAddDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        System.out.println();
        this.kartService.kartAdd(kartAddDto);
        return ResponseEntity.ok().body("장바구니에 등록되었습니다.");
    }

    @PostMapping("/api/kart/read")
    public ResponseEntity<?> kartAdd(@RequestBody @Valid KartReadDto kartReadDto, Errors errors ){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        return ResponseEntity.ok().body(this.kartService.kartRead(kartReadDto));
    }

    @PostMapping("/api/kart/eaupdate")
    public ResponseEntity<?> kartEaUpdate(@RequestBody @Valid KartEaUpdateDto kartUpdateDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        this.kartService.kartEaUpdate(kartUpdateDto);
        return ResponseEntity.ok().body("구매수량 수정을 완료했습니다.");
    }
    @PostMapping("/api/kart/saleupdate")
    public ResponseEntity<?> kartSaliUpdate(@RequestBody @Valid KartSaleUpdateDto kartSaleUpdateDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        this.kartService.kartSaleUpdate(kartSaleUpdateDto);
        return ResponseEntity.ok().body("Sale 항목 수정을 완료했습니다.");
    }
    @PostMapping("/api/kart/delete")
    public ResponseEntity<?> kartSalUpdate(@RequestBody @Valid KartDeleteDto kartDeleteDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        System.out.println();
        this.kartService.kartDeleteDto(kartDeleteDto);
        return ResponseEntity.ok().body("삭제 하엿습니다.");
    }
}