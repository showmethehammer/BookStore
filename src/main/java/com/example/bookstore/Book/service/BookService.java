package com.example.bookstore.Book.service;

import com.example.bookstore.Book.Dto.*;
import com.example.bookstore.Book.entity.Book;
import com.example.bookstore.Book.entity.Comment;
import com.example.bookstore.Book.exception.*;
import com.example.bookstore.Book.repository.BookRepository;
import com.example.bookstore.Book.repository.CommentRepository;
import com.example.bookstore.Book.search.BookSearchType;
import com.example.bookstore.login.exception.BookUserException;
import com.example.bookstore.login.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;

    // 카카오 라이센스
    @Value("${kokoao.api.key}")
    private String KaKaoKey;
    // 카카오 API검색 기본주소
    private String url = "https://dapi.kakao.com/v3/search/book";


    /**
     * 책 검색용 API
     * @param query     검색어
     * @param page      검색 List page 10권씩 끊어서 보냄
     * @param bookType  검색종류
     * @param sort      정렬 순서 1. 정확도순  2. 출간일순   그외. 정확도순
     * @return
     */
    public ResponseEntity<Map> bookSearch(String query, Integer page,
                                          Integer bookType, Integer sort) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        // 페이지를 잘못 입력한 경우 1로 변경
        if (page < 1) {
            page = 1;
        }
        // 일년번호 기준일 경우
        if(sort == 2){
            if(query.indexOf(" ") >= 0) {
                String isbnCh = query.substring(0, query.indexOf(" "));
                query = isbnCh;
            }
        }
        // 검색순서 1. 검색어 일치기준   2. 출판일 기준   그외. 검색어 일치 기준
        String sortSt = BookSearchType.sort(sort);
        // 검색Type 1. 제목   2. 국제표증 등록번호
        //         3. 출판사  4. 사람이름    그외. 제목
        String bookTypeSt = BookSearchType.bookType(bookType);
        // 카카오 API 라이센스
        httpHeaders.set("Authorization", "KakaoAK " + KaKaoKey);
        // 검색해서 나온값을 담아줌.
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        URI targetUrl = UriComponentsBuilder
                .fromUriString(this.url)
                .queryParam("query", query)
                .queryParam("size", 20)
                .queryParam("page", page)
                .queryParam("target", bookTypeSt)
                .queryParam("sort", sortSt)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        // 담은 값을 출력함.
        return restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Map.class);
    }


    /**
     * 책 등록 및 Update
     * Api로 불러와서 처리하는 상황에서 creat와 update가 동일할것이라고 판단해
     * create는 만들지 안음.
     * @param bookUpdateDto
     * @return
     */
    public ResponseEntity<?> bookUpdate(BookUpdateDto bookUpdateDto) {
        // 책을 검색
        Optional<Book> opBook = bookRepository.findByIsbnContaining(bookUpdateDto.getIsbn());
        // 할인율 변환
        Integer Sale = Integer.parseInt(bookUpdateDto.getSale());
        // 수량 변환
        Integer statusEa = Integer.parseInt(bookUpdateDto.getStatusEa());
        if(Sale == null ||  Sale < 0 || Sale > 100) {
            throw new BookException(BookErrorCode.BOOK_NOT_SALE_NUMBER_ERROR);
        }
        if(statusEa == null || statusEa < 0){
            throw new BookException(BookErrorCode.BOOK_NOT_COUNT_MINUS_ERROR);
        }
        if (!opBook.isEmpty()) {
            Book book = opBook.get();
            book.setStatusEa(statusEa);
            book.setSale(Sale);
            book.setSale_price((book.getPrice()
                    - ((book.getPrice() * book.getSale())/100)));
            bookRepository.save(book);

        } else {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "KakaoAK " + KaKaoKey);
            HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
            URI targetUrl = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("query", bookUpdateDto.getIsbn())
                    .queryParam("target", BookSearchType.bookType(2))
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();
            ResponseEntity<Map> bookData = restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Map.class);

            JSONObject jObj = new JSONObject(bookData.getBody());
            JSONArray memberArray = jObj.getJSONArray("documents");
            if(memberArray.length() == 0){
                throw new BookException(BookErrorCode.BOOK_NOT_FOUND_ERROR);
            }
            Book[] book = new Book[memberArray.length()];
            System.out.println("=====Members=====");
            String authors = "";
            String translators = "";
            for (int i = 0; i < memberArray.length(); i++) {
                JSONObject tempObj = (JSONObject) memberArray.get(i);
                authors = tempObj.get("authors").toString();
                translators = tempObj.get("translators").toString();
                book[i] = Book.builder()
                        .title((String)tempObj.get("title"))
                        .contents((String)tempObj.get("contents"))
                        .url((String)tempObj.get("url"))
                        .isbn((String)tempObj.get("isbn"))
                        .datetime((String)tempObj.get("datetime"))
                        .authors(authors)
                        .publisher((String)tempObj.get("publisher"))
                        .translators(translators)
                        .price((Integer)tempObj.get("price"))
                        .sale_price(((Integer)tempObj.get("price")
                                - ((Integer)tempObj.get("price")
                                * Sale)/100))
                        .thumbnail((String)tempObj.get("thumbnail"))
                        .statusEa(statusEa)
                        .sale(Sale)
                        .build();
            }
            bookRepository.save(book[0]);
        }
        return ResponseEntity.ok().body("책등록을 완료하였습니다.");
    }





    /**
     * 국제표준 등록번호를 이용하여 DataBase에입력해 서치가 되면 서치된 값을 반환
     * 시착 안되면 API에 검색해서 반환
     * 댓글도 함깨 검색해서 함깨 반환
     *
     * @param isbn 국제 표준 등록번호 입력
     * @return
     */
    public ResponseEntity<?> bookStatus(String isbn) {
        // 띄어쓰기가 있을시 API 호출이 안되는것을 확인하였고 국제호출번호를 따지면 2가지가 API로 받기때문에,
        // 2가지중에 한가지만 입력을 받으면 되는상황이어 띄어쓰기가 있을때 2가지를 입력했다고 판단하여, 첫번째 띄어쓰기 전까지만 읽어
        // 검색하는것을 구현
        if(isbn.indexOf(" ") >= 0) {
            String isbnCh = isbn.substring(0, isbn.indexOf(" "));
            isbn = isbnCh;
        }
        Optional<Book> opBook = bookRepository.findByIsbnContaining(isbn);
        if (!opBook.isEmpty()) {
            Book book = opBook.get();
            if(book.getSale() <= 0 || book.getSale() == null){
                book.setSale(0);
            }
            return ResponseEntity.ok().body(BookStatusDto.builder()
                    .title(book.getTitle())
                    .contents(book.getContents())
                    .url(book.getUrl())
                    .isbn(book.getIsbn())
                    .datetime(book.getDatetime())
                    .authors(book.getAuthors())
                    .publisher(book.getPublisher())
                    .translators(book.getTranslators())
                    .price(book.getPrice())
                    .sale_price(book.getPrice()-book.getPrice()*book.getSale()/100)
                    .thumbnail(book.getThumbnail())
                    .statusEa(book.getStatusEa())
                    .build());
        } else {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "KakaoAK " + KaKaoKey);
            HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
            URI targetUrl = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("query", isbn)
                    .queryParam("target", BookSearchType.bookType(2))
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();
            ResponseEntity<Map> bookData = restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Map.class);

            JSONObject jObj = new JSONObject(bookData.getBody());
            JSONArray memberArray = jObj.getJSONArray("documents");

            BookStatusDto[] bookStatusDtos = new BookStatusDto[memberArray.length()];
            System.out.println("=====Members=====");
            String authors = "";
            String translators = "";
            for (int i = 0; i < memberArray.length(); i++) {
                JSONObject tempObj = (JSONObject) memberArray.get(i);
                authors = tempObj.get("authors").toString();
                translators = tempObj.get("translators").toString();
                bookStatusDtos[i] = BookStatusDto.builder()
                        .title((String)tempObj.get("title"))
                        .contents((String)tempObj.get("contents"))
                        .url((String)tempObj.get("url"))
                        .isbn((String)tempObj.get("isbn"))
                        .datetime((String)tempObj.get("datetime"))
                        .authors(authors)
                        .publisher((String)tempObj.get("publisher"))
                        .translators(translators)
                        .price((Integer)tempObj.get("price"))
                        .sale_price((Integer)tempObj.get("price"))
                        .thumbnail((String)tempObj.get("thumbnail"))
                        .statusEa(0)
                        .build();
            }
            Map<String, Object> map = new HashMap<>();
            List<Comment> commentList = commentRepository.findByIsbnContaining(isbn);
            map.put("book",bookStatusDtos[0]);
            map.put("commentList",commentList);
            return ResponseEntity.ok().body(map);
        }
    }


    /**
     * 댓글 추가
     *
     * @param commentAddDto 댓글정보 객체
     * @return 완료 반환.
     */
    public String commentAdd(CommentAddDto commentAddDto) {
        Comment comment = Comment.builder()
                .isbn(commentAddDto.getIsbn())
                .dateTime(LocalDateTime.now())
                .userName(commentAddDto.getUserName())
                .text(commentAddDto.getText())
                .build();
        commentRepository.save(comment);
        return "댓글 추가를 완료하였습니다.";
    }

    /**
     * 댓글 수정
     * @param commentUpdateDto 수정 값을 가진 객체
     */
    public void commentUpdate(CommentUpdateDto commentUpdateDto) {
        // 댓글 찾기
        Comment comment = this.commentRepository.findById(Long.parseLong(commentUpdateDto.getId()))
                .orElseThrow(()-> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND_ERROR));
        // 아이디와 메치
        if(!comment.getUserName().equals(commentUpdateDto.getUserName())){
           throw new CommentException(CommentErrorCode.COMMENT_NOT_MATCH_ID_ERROR);
        }
        // text 변경
        comment.setText(commentUpdateDto.getText());
        this.commentRepository.save(comment);
    }


    /**
     * 댓글 삭제
     * @param commentDelDto 삭제 Data
     */
    public void commentDel(CommentDelDto commentDelDto) {
        // 댓글 찾기
        Comment comment = this.commentRepository.findById(Long.parseLong(commentDelDto.getId()))
                .orElseThrow(()-> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND_ERROR));
        // ID와 댓글 매칭
        if(!comment.getUserName().equals(commentDelDto.getUserName())){
            throw new BookUserException(UserErrorCode.USER_NOT_FOUND_ERROR);
        }
        // 삭제
        this.commentRepository.delete(comment);
    }

    /**
     *  아이디별 댓글 서치
     * @param userid 확인하고 싶은 아이디 입력
     * @param page 페이지
     * @return 서치한 Data와 Data개수 , page 수 반환.
     */
    public ResponseEntity<?> commentSearchId(String userid, Integer page) {
        // 출력 개수
        int pageEa = 10;
        // 댓글 전체 검색
        List<Comment> commentList = this.commentRepository.findAllByUserName(userid);
        // 댓글이 없으면 return
        if(commentList.size() == 0){
            return ResponseEntity.ok().body("댓글이 없습니다.");
        }
        // 출력용 댓글 저장공간 생성
        List<Comment> outlist = new ArrayList<>();
        int pageOut = page;
        // 요쳥한 page 보다 수량이 적을시
        if(commentList.size() < page * pageEa){
            pageOut = commentList.size()/page;
            for (int i = pageOut*10; i < commentList.size() ; i++) {
                outlist.add(commentList.get(i));
            }
        } else{ // Page만큼 출력할 충분한 양이 있을때.
            pageOut = (pageOut-1)*10;
            for (int i = pageOut ; i < pageOut+pageEa ; i++) {
                outlist.add(commentList.get(i));
            }
        }
        PageOrCountDto pageOrCountDto = new PageOrCountDto();
        pageOrCountDto.setCount(commentList.size());
        pageOrCountDto.setPageSize((commentList.size()/10)+1);
        Map<String, Object> map = new HashMap<>();
        map.put("comment",commentList);
        map.put("pageOrCount",pageOrCountDto);
        return ResponseEntity.ok().body(map);
    }


}