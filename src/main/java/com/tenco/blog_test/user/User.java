package com.tenco.blog_test.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor // JPA 엔티티 생성 시 필수
@Table (name="user_tb")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유니크 제약
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;

    private String password;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}