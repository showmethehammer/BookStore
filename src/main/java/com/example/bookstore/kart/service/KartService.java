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
import com.example.bookstore.login.entity.Member;
import com.example.bookstore.login.exception.BookUserException;
import com.example.bookstore.login.exception.UserErrorCode;
import com.example.bookstore.login.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class KartService {
    private final KartRepository kartRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public void kartAdd(KartAddDto kartAddDto) {
        // isbn 저장용
        String isbn = kartAddDto.getIsbn();
        int sale = Integer.parseInt(kartAddDto.getSale());
        // parseInt 저장용
        int ea = Integer.parseInt(kartAddDto.getEa());
        // 구매 개수가 0이거나 0보다 작을경우 Exception
        if (ea <= 0) {
            throw new KartErrorException(KartErrorCode.KART_NOT_INSERT_ZERO_ERROR);
        }
        // 국제표준 번호 2가지중에 한가지를 사용하기위한 코드
        if (kartAddDto.getIsbn().indexOf(" ") >= 0) {
            isbn = kartAddDto.getIsbn().substring(0, kartAddDto.getIsbn().indexOf(" "));
        }
        // 저장된 책이 있는지 확인.
        Book book = this.bookRepository.findByIsbnContaining(isbn)
                .orElseThrow(() -> new BookException(BookErrorCode.BOOK_NOT_FOUND_ERROR));
        // Member를 확인하고 없으면 알람.
        Member member = this.memberRepository.findByUserName(kartAddDto.getUsername())
                .orElseThrow(() -> new BookUserException(UserErrorCode.USER_ID_NOT_EXIST));
        // 입력값이 책의 개수보다 크면 알람
        if (book.getStatusEa() < ea) {
            throw new KartErrorException(KartErrorCode.KART_BOOK_MORE_MANY);
        }
        // 장바구니에 있는지 확인
        List<Kart> karts = kartRepository.findAllByUserName(kartAddDto.getUsername());
        Kart kart = null;
        for (int i = 0; i < karts.size(); i++) {
            if (karts.get(i).getIsbn().contains(isbn)) {
                kart = karts.get(i);
                break;
            }
        }
        // 장바구니에 있으면 장바구니 내용을 수정;
        if (kart != null) {
            kart.setEa(ea);
            kart.setSale(sale);
            kart.setSale_price(book.getPrice() - ((book.getPrice() * sale) / 100));
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
                    .authors(book.getAuthors())
                    .build();
        }
        this.kartRepository.save(kart);
    }

    public void kartEaUpdate(KartEaUpdateDto kartUpdateDto) {
        String isbn = kartUpdateDto.getIsbn();
        Integer statusEa = Integer.parseInt(kartUpdateDto.getEa());
        if (statusEa == null || statusEa < 0) {
            throw new KartErrorException(KartErrorCode.KART_SCARCE_BOOK_ERROR);
        }
        if (kartUpdateDto.getIsbn().indexOf(" ") >= 0) {
            isbn = kartUpdateDto.getIsbn().substring(0, kartUpdateDto.getIsbn().indexOf(" "));
        }
        Kart kart = this.kartRepository.findByIsbnContaining(isbn)
                .orElseThrow(() -> new KartErrorException(KartErrorCode.KART_NOT_FOUND_ERROR));
        Book book = bookRepository.findByIsbnContaining(isbn).orElseThrow(()
                -> new BookException(BookErrorCode.BOOK_NOT_FOUND_ERROR));
        if (book.getStatusEa() < statusEa) {
            throw new KartErrorException(KartErrorCode.KART_SCARCE_BOOK_ERROR);
        }
        kart.setEa(statusEa);
        kartRepository.save(kart);
    }

    public void kartSaleUpdate(KartSaleUpdateDto kartSaleUpdateDto) {
        String isbn = kartSaleUpdateDto.getIsbn();
        Integer sale = Integer.parseInt(kartSaleUpdateDto.getSale());
        if (sale == null || sale < 0 || sale > 100) {
            throw new KartErrorException(KartErrorCode.KART_SCARCE_BOOK_ERROR);
        }
        if (kartSaleUpdateDto.getIsbn().indexOf(" ") >= 0) {
            isbn = kartSaleUpdateDto.getIsbn().substring(0, kartSaleUpdateDto.getIsbn().indexOf(" "));
        }
        Kart kart = this.kartRepository.findByIsbnContaining(isbn)
                .orElseThrow(() -> new KartErrorException(KartErrorCode.KART_NOT_FOUND_ERROR));
        kart.setSale_price(kart.getPrice() - ((kart.getPrice() * sale) / 100));
        kart.setSale(sale);
        kartRepository.save(kart);
    }

    public void kartDeleteDto(KartDeleteDto kartDeleteDto) {
        List<Kart> karts = kartRepository.findAllByUserName(kartDeleteDto.getUsername());
        String isbn = kartDeleteDto.getIsbn();
        if (kartDeleteDto.getIsbn().indexOf(" ") >= 0) {
            isbn = kartDeleteDto.getIsbn().substring(0, kartDeleteDto.getIsbn().indexOf(" "));
        }
        Kart kart = null;
        for (int i = 0; i < karts.size(); i++) {
            if (karts.get(i).getIsbn().contains(isbn)) {
                kart = karts.get(i);
                break;
            }
        }
        if (kart == null) {
            throw new KartErrorException(KartErrorCode.KART_NOT_FOUND_ERROR);
        }
        this.kartRepository.delete(kart);
    }

    public List<Kart> kartRead(KartReadDto kartReadDto) {
        List<Kart>karts = kartRepository.findAllByUserName(kartReadDto.getUsername());
        return karts;
    }
}
