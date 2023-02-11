package com.example.bookstore.login.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Column(columnDefinition = "VARCHAR(20) NOT NULL")
    String userName;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(columnDefinition = "VARCHAR(20) NOT NULL")
    private String name;
    @Column(columnDefinition = "VARCHAR(50) NOT NULL")
    private String email;
    @Column(columnDefinition = "VARCHAR(15) NOT NULL")
    private String phone;
    @NotNull
    private String password;
    @NotNull
    private LocalDateTime authDate;
    private boolean idAuth;
    private String idAuthKey;
    @NotNull
    private String address1;
    @NotNull
    private String addressData1;
    private String address2;
    private String addressData2;


}
