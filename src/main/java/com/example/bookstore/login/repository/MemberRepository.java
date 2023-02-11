package com.example.bookstore.login.repository;


import com.example.bookstore.login.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member,String> {
    Optional<Member> findByUserName(String username);

    //    Optional<Member> findByUsernameAndIdAuthKey(String bookUserId, String bookUserIdAuthKey);
    boolean existsByUserName(String BookUserId);
}
