package com.example.bookstore.login.controller;

import com.example.bookstore.login.Dto.JoinDto;
import com.example.bookstore.login.Dto.LoginDto;
import com.example.bookstore.login.entity.Member;
import com.example.bookstore.login.exception.ValidException;
import com.example.bookstore.login.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MemberSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberRestController {

    public final MemberService memberService;


    /**
     * 회원 가입
     * @param joinDto 회원정보 입력
     * @param errors 에러확인
     * @return 상태 확인
     */
    @PostMapping("/api/member/join")
    public ResponseEntity<?> join(@RequestBody @Valid JoinDto joinDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        System.out.println(joinDto);
        this.memberService.join(joinDto);
        return ResponseEntity.ok().body("회원가입이 완료되었습니다.");
    }

    /**
     * 본인인증 Email확인
     * Email링크를 클릭하면 아이디와 인증키 비교하여 인증 완료.
     * @param AuthKey
     * @param username
     * @return
     */
    @GetMapping("/api/member/email/key/{AuthKey}/id/{username}")
    public ResponseEntity<?> authKey(@PathVariable String AuthKey, @PathVariable String username){
        return this.memberService.authKey(AuthKey,username);
    }


    @PostMapping("/api/member/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        String token = this.memberService.login(loginDto);
        return ResponseEntity.ok().body(token);
    }
    @PostMapping("/test")
    public ResponseEntity<?> test(Authentication authentication){
        return ResponseEntity.ok().body(authentication.getName());
    }

}
