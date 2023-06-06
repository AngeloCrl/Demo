package com.app.demo.user.repository;


import com.app.demo.user.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByToken(String token);

    List<Token> findTokensByUserId(Long userId);

    @Transactional
    void deleteByToken(String token);

    List<Token> findByUserId(Long userId);

}
