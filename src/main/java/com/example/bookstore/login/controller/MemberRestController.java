package com.example.bookstore.login.controller;

import com.example.bookstore.login.Dto.*;
import com.example.bookstore.login.exception.ValidException;
import com.example.bookstore.login.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 회원 정보 객체를 받아서 DataBase에 등록함.
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
     * @param AuthKey 인증키
     * @param username userName
     * @return
     */
    @GetMapping("/api/member/email/key/{AuthKey}/id/{username}")
    public ResponseEntity<?> authKey(@PathVariable String AuthKey, @PathVariable String username){
        return this.memberService.authKey(AuthKey,username);
    }


    /**
     * 로그인
     * ID와 비밀번호를 활용하여 토큰을 생성하고, 그 토큰을 확용하여 page를 이돌할 수 있도록함.
     *
     * @param loginDto ID와 비밀번호가 담긴 Data
     * @param errors
     * @return
     */
    @PostMapping("/api/member/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        // ID와 비밀번호를 활용하여 토큰을 생성.
        String token = this.memberService.login(loginDto);
        // 토큰을 반환함.
        return ResponseEntity.ok().body(token);
    }


    /**
     * 중복 ID 확인
     * ID만 있는 객체를 받아서 DataBase에 동일한 ID가 있는지를 확인함.
     * @param idCheckDto 아이디 전송
     * @param errors 입력이 안된경우 알람
     * @return
     */
    @PostMapping("/api/member/idcheck")
    public ResponseEntity<?> idCheck (@RequestBody @Valid IdCheckDto idCheckDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        if(this.memberService.idCheckDto(idCheckDto.getUserName())){
            return ResponseEntity.badRequest().body("중복되는 ID가 있습니다.");
        }
        return ResponseEntity.ok().body("사용할 수 있는 ID입니다.");
    }


    /**
     * 비밀번호 변경
     * ID와, 비밀번호, 새비밀번호를 ID와 비밀번호를 확인후 새비밀번호로 변경.
     * @param passwordChange
     * @param errors
     * @return
     */
    @PostMapping("api/member/password/change")
    public ResponseEntity<?> passwordChange(@RequestBody @Valid MemberStatusChangeDto.PasswordChange passwordChange, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        this.memberService.passwordChange(passwordChange);
        return ResponseEntity.ok().body("비밀번호 변경을 완료 하였습니다.");
    }


    /**
     * 테스트
     * 코드 검증용 테스트 그외 활용도 없음.
     * @param authentication
     * @return
     */
    @PostMapping("/test")
    public ResponseEntity<?> test(Authentication authentication){
        return ResponseEntity.ok().body(memberService.test());
    }


    /**
     * 회원 기본 정보 변경
     * 비밀번호를 제외한 전체적인 정보를 한번에 변경함.
     *
     * @param memberStatusChange 변경정보가 담긴 객체
     * @param errors
     * @return
     */
    @PostMapping("api/member/status/change")
    public ResponseEntity<?> statusChange(@RequestBody @Valid MemberStatusChangeDto memberStatusChange, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        this.memberService.memberStatusChange(memberStatusChange);
        return ResponseEntity.ok().body("회원정보 변경이 완료되었습니다.");
    }


    /**
     *  아이디 분실로인한 Email요청
     *
     *  핸드폰번호, Email, 이름 을 매칭하여 검색.
     *  3개중 하나라도 다르면 알람.
     *
     * @param idSearch 핸드폰번호, Email, 이름
     * @param errors
     * @return
     */
    @PostMapping("api/member/id/search")
    public ResponseEntity<?> idSearch (@RequestBody @Valid IdSearch idSearch, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        this.memberService.idSearch(idSearch);
        return ResponseEntity.ok().body("ID를 Email로 전송하였습니다.");
    }


    /**
     * 비밀번호 찾기
     * 아이디, 이름, 핸드폰번호를 확인하여 일치할경우
     * 임시비밀번호를 Email로 전송.
     *
     * @param passwordSearch ID, 비밀번호, 이름 입력.
     * @param errors
     * @return
     */
    @PostMapping("/api/member/password/search")
    public ResponseEntity<?> passwordSearch (@RequestBody @Valid PasswordSearch passwordSearch, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        this.memberService.passwordSearch(passwordSearch);
        return ResponseEntity.ok().body("임시비밀번호를 보냈습니다.");
    }
}