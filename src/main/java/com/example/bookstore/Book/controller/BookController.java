package com.example.bookstore.Book.controller;

import com.example.bookstore.Book.Dto.*;
import com.example.bookstore.Book.service.BookService;
import com.example.bookstore.login.exception.BookUserException;
import com.example.bookstore.login.exception.UserErrorCode;
import com.example.bookstore.login.exception.ValidException;
import com.example.bookstore.login.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
public class BookController {


    private final BookService bookService;
    private final MemberService memberService;


    @PostMapping("/api/book/update")
    public ResponseEntity<?> bookUpdate(@RequestBody @Valid BookUpdateDto bookCreateDto , Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        return bookService.bookUpdate(bookCreateDto);
    }

    /**
     * 책 검색용 API
     * @param query     책제목
     * @param page      검색페이지 기본 10개씩
     * @param bookType  검색 종류
     * @param sort      정렬 순서 1. 정확도순  2. 출간일순   그외. 정확도순
     * @return
     */
    @GetMapping("/api/book/namesearch/query/{query}/page/{page}/type/{bookType}/sort/{sort}")
    public Map bookSearch(@PathVariable String query, @PathVariable Integer page,
                          @PathVariable Integer bookType, @PathVariable Integer sort){
        ResponseEntity<Map> result = bookService.bookSearch(query,page,bookType,sort);
        return result.getBody();
    }

    /**
     *  잭을 선택하여 상세정보를 확인하기 위한 용도
     *  책 검색용 API에서 리스트가 나오면 한가지를 클릭하면 이벤트로
     *  국제표준 일년번호가 들어가 상세정보를 보내는 것을 그리며 구현
     *
     * @param isbn 국제표준 등록번호 입력
     * @return 등록번호에 맞는 Data 반환
     */
    @GetMapping("/api/book/status/{isbn}")
    public ResponseEntity<?> bookStatus(@PathVariable String isbn){
        return ResponseEntity.ok().body(bookService.bookStatus(isbn));
    }

    /**
     * 댓글을 추가하는 API
     *
     * @param commentAddDto 댓글 정보 객체
     * @param errors
     * @return
     */
    @PostMapping("/api/book/comment/add")
    public ResponseEntity<?> commentAdd(@RequestBody @Valid CommentAddDto commentAddDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        return ResponseEntity.ok().body(this.bookService.commentAdd(commentAddDto));
    }

    /**
     * 댓글을 수정하는 객체
     *
     * @param commentUpdateDto  댓글을 수정을위한 정보를 담은 객체
     * @param errors
     * @return 완료 만 반환.
     */
    @PostMapping("/api/book/comment/update")
    public ResponseEntity<?> commentUpdate(@RequestBody @Valid CommentUpdateDto commentUpdateDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        if(!memberService.idPasswordCheck(commentUpdateDto.getUserName(), commentUpdateDto.getPassword())){
            throw new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR);
        }
        this.bookService.commentUpdate(commentUpdateDto);
        return ResponseEntity.ok().body("댓글 수정을 완료하였습니다.");
    }


    /**
     * 댓글삭제 구현
     * @param commentDelDto 삭제를 위한 정보
     * @param errors error
     * @return 완료신호
     */
    @PostMapping("/api/book/comment/dell")
    public ResponseEntity<?> commentDel(@RequestBody @Valid CommentDelDto commentDelDto, Errors errors){
        if(errors.hasErrors()){
            List<ObjectError> error = errors.getAllErrors();
            throw new ValidException(error.get(0).getCode(),error.get(0).getDefaultMessage());
        }
        if(!memberService.idPasswordCheck(commentDelDto.getUserName(), commentDelDto.getPassword())){
            throw new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR);
        }
        this.bookService.commentDel(commentDelDto);
        return ResponseEntity.ok().body("댓글을 삭제 하였습니다.");
    }

    /**
     * 해당 ID의 댓글 확인
     * @param userid ID 입력
     * @param page 출력 베이지 10개씨 끊어서 나옴.
     * @return Json으로 댓글 목록 반환.
     */
    @GetMapping("/api/book/comment/{userid}/{page}")
    public ResponseEntity<?> commentSearchId(@PathVariable String userid, @PathVariable Integer page){
        return this.bookService.commentSearchId(userid,page);
    }
}
