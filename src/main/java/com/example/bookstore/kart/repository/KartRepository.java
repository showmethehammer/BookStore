package com.example.bookstore.kart.repository;

import com.example.bookstore.kart.entity.Kart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KartRepository extends JpaRepository<Kart,String> {
    Optional<Kart> findByIsbnContaining(String isbn);
}
