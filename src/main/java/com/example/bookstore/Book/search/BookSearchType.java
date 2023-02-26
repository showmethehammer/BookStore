package com.example.bookstore.Book.search;

public class BookSearchType {
    public static String bookType (Integer bookType){
        switch(bookType){
            case 2:{
                return "isbn"; // 국체표준 등록번호
            }
            case 3:{
                return "publisher";  // 출판사
            }
            case 4:{
                return "person";  // 인명
            }
            default:{
                return "title"; // 제목
            }
        }
    }
    public static String sort(Integer sort){
        if(sort == 2){
            return "latest";
        }
        return "accuracy";
    }
}
