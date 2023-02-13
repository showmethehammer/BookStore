package com.example.bookstore.Book.repository;

import com.example.bookstore.Book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,String> {
    Optional<Book> findByIsbnContaining(String isbn);

}
