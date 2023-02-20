package com.example.bookstore.kart.controller;

import com.example.bookstore.kart.Dto.*;
import com.example.bookstore.kart.service.KartService;
import com.example.bookstore.login.exception.ValidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
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

    /**
     * 장바구니 등록
     * 아이디, 책정보, 수량을 확인하여 DataBase에 저장.
     *
     * @param kartAddDto 회원과 ,책정보, 수량 이 담긴 객체
     * @param errors
     * @return
     */
    @PostMapping("/api/kart/add")
    public ResponseEntity<?> kartAdd(@RequestBody @Valid KartAddDto kartAddDto, Errors errors){
        // Error 확인
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        // 책 등록
        this.kartService.kartAdd(kartAddDto);
        return ResponseEntity.ok().body("장바구니에 등록되었습니다.");
    }


    /**
     * 장바구니 내용 불러오기
     * ID를 확인하여 해당하는 장바구니 목록을 불러옴.
     * @param kartReadDto ID 정보가 담긴 객체
     * @param errors
     * @return
     */
    @PostMapping("/api/kart/read")
    public ResponseEntity<?> kartAdd(@RequestBody @Valid KartReadDto kartReadDto, Errors errors ){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        return ResponseEntity.ok().body(this.kartService.kartRead(kartReadDto));
    }

    /**
     * 구매 수량 수정
     * 아이디, 책번호, 구매수량을 입력받아 구매수량 수정.
     * @param kartUpdateDto ID, 책번호, 구매수량이 있는 객체
     * @param errors
     * @return
     */
    @PostMapping("/api/kart/eaupdate")
    public ResponseEntity<?> kartEaUpdate(@RequestBody @Valid KartEaUpdateDto kartUpdateDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        this.kartService.kartEaUpdate(kartUpdateDto);
        return ResponseEntity.ok().body("구매수량 수정을 완료했습니다.");
    }

    /**
     * 할인율 수정
     * 아이디, 책번호, 할인율을 입력받아 장바구니 DAta 수정
     * @param kartSaleUpdateDto 아이디, 책번호, 할인율읖 포함한 객체
     * @param errors
     * @return
     */
    @PostMapping("/api/kart/saleupdate")
    public ResponseEntity<?> kartSaliUpdate(@RequestBody @Valid KartSaleUpdateDto kartSaleUpdateDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        this.kartService.kartSaleUpdate(kartSaleUpdateDto);
        return ResponseEntity.ok().body("Sale 항목 수정을 완료했습니다.");
    }

    /**
     * 카트에 등록된 책 삭제
     * ID와 책등록번호를 확인하여 삭제
     * @param kartDeleteDto ID와 책등록번호가 담긴 객체
     * @param errors
     * @return
     */
    @PostMapping("/api/kart/delete")
    public ResponseEntity<?> kartSalUpdate(@RequestBody @Valid KartDeleteDto kartDeleteDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        this.kartService.kartDeleteDto(kartDeleteDto);
        return ResponseEntity.ok().body("삭제 하엿습니다.");
    }

    /**
     * 구매 선택 여부 수정.
     * ID, 비밀번호, 책구매여부를 확인하여 값 적용.
     * @param kartCheckingDto ID,비밀번호,책구매여부 를 확인할수 있는 객체
     * @param errors
     * @return
     */
    @PostMapping("/api/kart/checking")
    public ResponseEntity<?> kartChecking(@RequestBody @Valid KartCheckingDto kartCheckingDto,Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        this.kartService.kartChecking(kartCheckingDto);
        return ResponseEntity.ok().body("변경 완료");
    }
}