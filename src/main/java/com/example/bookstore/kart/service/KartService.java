package com.example.bookstore.kart.service;

import com.example.bookstore.Book.entity.Book;
import com.example.bookstore.Book.exception.BookErrorCode;
import com.example.bookstore.Book.exception.BookException;
import com.example.bookstore.Book.repository.BookRepository;
import com.example.bookstore.kart.Dto.*;
import com.example.bookstore.kart.entity.Kart;
import com.example.bookstore.kart.excption.KartErrorCode;
import com.example.bookstore.kart.excption.KartErrorException;
import com.example.bookstore.kart.repository.KartRepository;
import com.example.bookstore.kart.search.SearchFilter;
import com.example.bookstore.login.entity.Member;
import com.example.bookstore.login.exception.BookUserException;
import com.example.bookstore.login.exception.UserErrorCode;
import com.example.bookstore.login.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class KartService {
    private final KartRepository kartRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    /**
     * 장부구니 등록
     * Step
     * 1. 국제표준코드 분리 (카카오에 2가지가 번호가 들어가 있는데 2가지를 전부 입력할 경우 검색이 안되는 상황이 있었음.
     * 2. 형변환 String으로 들어온 숫자Data 형변환. 및 오기에대한 알람.
     * 3. DataBase에서 책 확인
     * 4. User 검색 확인
     * 5. 구매수량, 재고 확인
     * 6. 장바구니에 등록돼있는지 확인하고 있으면 Update 없으면 추가.
     * @param kartAddDto
     */
    public void kartAdd(KartAddDto kartAddDto) {

        // 1. 국제표준코드 분리 (카카오에 2가지가 번호가 들어가 있는데 2가지를 전부 입력할 경우 검색이 안되는 상황이 있었음.
        // isbn 저장용, 국제표준 번호 2가지중에 한가지를 사용하기위한 코드
        String isbn = SearchFilter.isbnSpaseDel(kartAddDto.getIsbn());

        // 2. 형변환 String으로 들어온 숫자Data 형변환. 및 오기에대한 알람.
        int sale = Integer.parseInt(kartAddDto.getSale());
        // parseInt 저장용
        int ea = Integer.parseInt(kartAddDto.getEa());
        // 구매 개수가 0이거나 0보다 작을경우 Exception
        if (ea <= 0) {
            throw new KartErrorException(KartErrorCode.KART_NOT_INSERT_ZERO_ERROR);
        }

        //3. DataBase에서 책 확인
        // 저장된 책이 있는지 확인.
        Book book = this.bookRepository.findByIsbnContaining(isbn)
                .orElseThrow(() -> new BookException(BookErrorCode.BOOK_NOT_FOUND_ERROR));

        // 4. User 검색 확인
        // Member를 확인하고 없으면 알람.
        Member member = this.memberRepository.findByUserName(kartAddDto.getUsername())
                .orElseThrow(() -> new BookUserException(UserErrorCode.USER_ID_NOT_EXIST));

        //5. 구매수량, 재고 확인
        // 입력값이 책의 개수보다 크면 알람
        if (book.getStatusEa() < ea) {
            throw new KartErrorException(KartErrorCode.KART_BOOK_MORE_MANY);
        }

        // 6. 장바구니에 등록돼있는지 확인하고 있으면 Update 없으면 추가.
        // 장바구니에 있는지 확인
        List<Kart> karts = kartRepository.findAllByUserName(kartAddDto.getUsername());
        Kart kart =  SearchFilter.kartUserSearch(karts,isbn);
        // 장바구니에 있으면 장바구니 내용을 수정;
        if (kart != null) {
            kart.setEa(ea);
            kart.setSale(sale);
            kart.setSale_price(book.getPrice() - ((book.getPrice() * sale) / 100));
            kart.setRegDateTime(LocalDateTime.now());
            kart.setBuyOx(true);
            this.kartRepository.save(kart);
            return;
        } else {
            kart = Kart.builder()
                    .userName(member.getUserName())
                    .title(book.getTitle())
                    .isbn(book.getIsbn())
                    .thumbnail(book.getThumbnail())
                    .ea(ea)
                    .price(book.getPrice())
                    .sale_price((book.getPrice() - ((book.getPrice() * sale) / 100)))
                    .sale(sale)
                    .regDateTime(LocalDateTime.now())
                    .authors(book.getAuthors())
                    .buyOx(true)
                    .build();
        }
        this.kartRepository.save(kart);
    }

    /**
     * 책 구매수량 수정
     * Step
     * 1. 아이디에 책List 검색
     * 2. 일년번호를 활용하여 책 축출
     * 3. BookDataBase에서 책을 검색하여 개수비교
     * 4. 구매수량 수정값 입력
     * 5. 저장
     * @param kartUpdateDto
     */
    public void kartEaUpdate(KartEaUpdateDto kartUpdateDto) {
        String isbn = SearchFilter.isbnSpaseDel(kartUpdateDto.getIsbn());
        Integer statusEa = Integer.parseInt(kartUpdateDto.getEa());
        if (statusEa == null || statusEa < 0) {
            throw new KartErrorException(KartErrorCode.KART_SCARCE_BOOK_ERROR);
        }
        // 1. 아이디에 책List 검색
        List<Kart> karts = kartRepository.findAllByUserName(kartUpdateDto.getUsername());
        // 2. 일년번호를 활용하여 책 축출
        Kart kart = SearchFilter.kartUserSearch(karts,isbn);
        if (kart == null) {
            throw new KartErrorException(KartErrorCode.KART_BOOK_NO_STUFF);
        }
        // 3. BookDataBase에서 책을 검색하여 개수비교
        Book book = bookRepository.findByIsbnContaining(isbn).orElseThrow(()
                -> new BookException(BookErrorCode.BOOK_NOT_FOUND_ERROR));
        if (book.getStatusEa() < statusEa) {
            throw new KartErrorException(KartErrorCode.KART_SCARCE_BOOK_ERROR);
        }
        // 4. 구매수량 수정값 입력
        kart.setEa(statusEa);
        kart.setRegDateTime(LocalDateTime.now());
        // 5. 저장
        kartRepository.save(kart);
    }

    /**
     * 책 구입 할인율 수정.
     * Step
     * 1. Kart DataBase에서 책 검색
     * 2. 할인율 과 금액 수정
     * 3. DataBase에 저장
     * @param kartSaleUpdateDto
     */
    public void kartSaleUpdate(KartSaleUpdateDto kartSaleUpdateDto) {
        String isbn = SearchFilter.isbnSpaseDel(kartSaleUpdateDto.getIsbn());
        Integer sale = Integer.parseInt(kartSaleUpdateDto.getSale());
        if (sale == null || sale < 0 || sale > 100) {
            throw new KartErrorException(KartErrorCode.KART_SCARCE_BOOK_ERROR);
        }
        // 1. Kart DataBase에서 책 검색
        List<Kart> karts = kartRepository.findAllByUserName(kartSaleUpdateDto.getUsername());
        Kart kart = SearchFilter.kartUserSearch(karts,isbn);
        if (kart == null) {
            throw new KartErrorException(KartErrorCode.KART_BOOK_NO_STUFF);
        }
        // 2. 할인율 과 금액 수정
        kart.setSale_price(kart.getPrice() - ((kart.getPrice() * sale) / 100));
        kart.setSale(sale);
        kart.setRegDateTime(LocalDateTime.now());
        // 3. DataBase에 저장
        kartRepository.save(kart);
    }

    /**
     * Kart에 등록된 책 삭제
     * Step
     * 1. ID와 책번호를 이용하여 검색
     * 2. 삭제
     * @param kartDeleteDto
     */
    public void kartDeleteDto(KartDeleteDto kartDeleteDto) {
        // 1. ID와 책번호를 이용하여 검색
        List<Kart> karts = kartRepository.findAllByUserName(kartDeleteDto.getUsername());
        String isbn = SearchFilter.isbnSpaseDel(kartDeleteDto.getIsbn());
        Kart kart =  SearchFilter.kartUserSearch(karts,isbn);
        if (kart == null) {
            throw new KartErrorException(KartErrorCode.KART_NOT_FOUND_ERROR);
        }
        // 2. 삭제
        this.kartRepository.delete(kart);
    }

    /**
     * 장바구니 리스트
     * 아이디와 일치하는 상품을 찾아 리턴
     * Step
     *  - 아이디로 Kart DataBase를 검색하여 검색된 상품전체를 리턴
     */
    public List<Kart> kartRead(KartReadDto kartReadDto) {
        // 아이디로 Kart DataBase를 검색하여 검색된 상품전체를 리턴
        List<Kart>karts = kartRepository.findAllByUserName(kartReadDto.getUsername());
        return karts;
    }

    /**
     * 책 구매여부
     * Step
     * 1. 해당하는 책검색.
     * 2. 구매여부 변경.
     * @param kartCheckingDto
     */
    public void kartChecking(KartCheckingDto kartCheckingDto) {
        // 1. 해당하는 책 검색
        List<Kart> karts = kartRepository.findAllByUserName(kartCheckingDto.getUsername());
        String isbn = SearchFilter.isbnSpaseDel(kartCheckingDto.getIsbn());
        Kart kart =  SearchFilter.kartUserSearch(karts,isbn);
        if (kart == null) {
            throw new KartErrorException(KartErrorCode.KART_NOT_FOUND_ERROR);
        }
        // 2. 구매여부 변경.
        kart.setBuyOx(kartCheckingDto.isBuyOx());
        kartRepository.save(kart);


    }
}
