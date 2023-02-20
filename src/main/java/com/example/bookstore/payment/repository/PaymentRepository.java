package com.example.bookstore.payment.repository;

import com.example.bookstore.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentRepository extends JpaRepository<Payment,String> {

}
