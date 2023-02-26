package com.example.bookstore.kart.service;

import com.example.bookstore.Book.entity.Book;
import com.example.bookstore.Book.exception.BookErrorCode;
import com.example.bookstore.Book.exception.BookException;
import com.example.bookstore.Book.repository.BookRepository;
import com.example.bookstore.kart.Dto.KartAddDto;
import com.example.bookstore.kart.Dto.KartEaUpdateDto;
import com.example.bookstore.kart.Dto.KartSaleUpdateDto;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class KartService {
    private final KartRepository kartRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public void kartAdd(KartAddDto kartAddDto) {
        String isbn = kartAddDto.getIsbn();
        if(kartAddDto.getIsbn().indexOf(" ") >= 0) {
            isbn = kartAddDto.getIsbn().substring(0, kartAddDto.getIsbn().indexOf(" "));
        }
        Book book = this.bookRepository.findByIsbnContaining(isbn)
                .orElseThrow(() -> new BookException(BookErrorCode.BOOK_NOT_FOUND_ERROR));
        Member member = this.memberRepository.findByUserName(kartAddDto.getUserId())
                .orElseThrow(()-> new BookUserException(UserErrorCode.USER_ID_NOT_EXIST));
        Kart kart = Kart.builder()
                .id(member.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .thumbnail(book.getThumbnail())
                .ea(Integer.parseInt(kartAddDto.getEa()))
                .price(book.getPrice())
                .sale_price((book.getPrice()-((book.getPrice()*book.getSale())/100)))
                .sale(book.getSale())
                .authors(book.getAuthors())
                .build();
        kartRepository.save(kart);
    }

    public void kartEaUpdate(KartEaUpdateDto kartUpdateDto) {
        String isbn = kartUpdateDto.getIsbn();
        Integer statusEa = Integer.parseInt(kartUpdateDto.getEa());
        if(statusEa == null || statusEa < 0){
            throw new KartErrorException(KartErrorCode.KART_SCARCE_BOOK_ERROR);
        }
        if(kartUpdateDto.getIsbn().indexOf(" ") >= 0) {
            isbn = kartUpdateDto.getIsbn().substring(0, kartUpdateDto.getIsbn().indexOf(" "));
        }
        Kart kart = this.kartRepository.findByIsbnContaining(isbn)
                .orElseThrow(()-> new KartErrorException(KartErrorCode.KART_NOT_FOUND_ERROR));
        Book book = bookRepository.findByIsbnContaining(isbn).orElseThrow(()
                -> new BookException(BookErrorCode.BOOK_NOT_FOUND_ERROR));
        if(book.getStatusEa() < statusEa){
            throw new KartErrorException(KartErrorCode.KART_SCARCE_BOOK_ERROR);
        }
        kart.setEa(statusEa);
        kartRepository.save(kart);
    }

    public void kartSaleUpdate(KartSaleUpdateDto kartSaleUpdateDto) {
        String isbn = kartSaleUpdateDto.getIsbn();
        Integer sale = Integer.parseInt(kartSaleUpdateDto.getSale());
        if(sale == null || sale < 0 || sale > 100){
            throw new KartErrorException(KartErrorCode.KART_SCARCE_BOOK_ERROR);
        }
        if(kartSaleUpdateDto.getIsbn().indexOf(" ") >= 0) {
            isbn = kartSaleUpdateDto.getIsbn().substring(0, kartSaleUpdateDto.getIsbn().indexOf(" "));
        }
        Kart kart = this.kartRepository.findByIsbnContaining(isbn)
                .orElseThrow(()->new KartErrorException(KartErrorCode.KART_NOT_FOUND_ERROR));
        kart.setSale_price(kart.getPrice() - ((kart.getPrice() * sale) / 100));
        kart.setSale(sale);
        kartRepository.save(kart);
    }
}
