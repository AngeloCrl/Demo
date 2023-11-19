package com.app.demo.user.repository;


import com.app.demo.user.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByToken(String token);
    void deleteByToken(String token);
}
