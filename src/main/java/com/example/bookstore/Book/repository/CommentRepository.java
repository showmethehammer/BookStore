package com.example.bookstore.Book.repository;

import com.example.bookstore.Book.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,String> {
    List<Comment> findByIsbnContaining(String isbn);
    Optional<Comment> findById(Long id);
    List<Comment> findAllByUserName(String userName);
}
