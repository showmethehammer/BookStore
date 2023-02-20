package com.example.bookstore.kart.search;

import com.example.bookstore.kart.entity.Kart;

import java.util.List;

public class SearchFilter {
    static public Kart kartUserSearch(List<Kart> KartList, String isbn ){
        for (int i = 0; i < KartList.size(); i++) {
            if (KartList.get(i).getIsbn().contains(isbn)) {
                return KartList.get(i);
            }
        }
        return null;
    }
    static public String isbnSpaseDel(String isbn){
        if (isbn.indexOf(" ") >= 0) {
          return  isbn.substring(0, isbn.indexOf(" "));
        }
        return isbn;
    }
}
