package com.app.demo.user.service;

import com.app.demo.user.dto.jwt.TokenDto;
import com.app.demo.user.model.Token;
import com.app.demo.user.model.User;
import com.app.demo.user.model.UserTokenType;
import com.app.demo.user.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String validateToken(TokenDto tokenDto) {
        final Token passToken = tokenRepository.findByToken(tokenDto.getToken());
        return passToken == null ? "invalidToken" : isTokenExpired(passToken) ? "expired" : null;
    }

    private boolean isTokenExpired(Token passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    public String createUserToken(User user, UserTokenType userTokenType) {
        String token = UUID.randomUUID().toString();
        Timestamp expirationDate = new Timestamp(new Date().getTime());
        ZonedDateTime zonedDateTime = expirationDate.toInstant().atZone(ZoneId.of("UTC"));
        expirationDate = Timestamp.from(zonedDateTime.plus(1, ChronoUnit.DAYS).toInstant());
        Token myToken = Token.builder().user(user).token(token).userTokenType(userTokenType).expiryDate(expirationDate).build();
        tokenRepository.save(myToken);
        return token;
    }


    public void deleteUserTokenByToken(String token) {
        tokenRepository.deleteByToken(token);
    }
}
