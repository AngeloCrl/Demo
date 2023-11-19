package com.app.demo.security;

import com.app.demo.user.model.UserRole;
import com.app.demo.utils.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@Component
public class JwtTokenProvider {

    /**
     * THIS IS NOT A SECURE PRACTICE! For simplicity, we are storing a static key here.
     * Ideally, this key would be kept on a config-server.
     */

    private String secretKey;
    private long validityInMilliseconds; // 10 minutes
    private final MyUserDetails myUserDetails;

    public JwtTokenProvider(MyUserDetails myUserDetails,
                            @Value("${security.jwt.token.secret-key:secret-key}") String secretKey,
                            @Value("${security.jwt.token.expire-length:600000}") long validityInMilliseconds) {
        this.myUserDetails = myUserDetails;
        this.secretKey = secretKey;
        this.validityInMilliseconds = validityInMilliseconds;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String email, Set<UserRole> userRoles) {
        // Initialize a new Claims object, which represents the content of the JWT.
        // In this case, we set the subject of the JWT to be the user's email:
        Claims claims = Jwts.claims().setSubject(email);

        // Set additionally an "auth" claim in the JWT, which is used for storing the user's roles-authorities.
        // UserRole Class implements Spring Security's GrantedAuthority interface:
        claims.put("auth", userRoles
                .stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getAuthority())).toList()); //-> getAuthority() = RoleType(ROLE_ADMIN, ROLE_CLIENT etc.)

        Date now = new Date();
        Date validityTimePeriod = new Date(now.getTime() + validityInMilliseconds); // Set the validity time period for the created Token.

        // Construct and return the JWT:
        return Jwts.builder().setClaims(claims)
                .setIssuedAt(now).setExpiration(validityTimePeriod)
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public Authentication getAuthentication(String token) {
        String email = getEmail(token);
        UserDetails userDetails = myUserDetails.loadUserByUsername(email); // In MyUserDetails we've set: ..userdetails.User.withUsername(email)
        // Creates and return a UsernamePasswordAuthenticationToken using:
            // userDetails (The information about the user loaded from the database)
            // An empty string representing the user's password. In this context, it's empty because the token is already validated and there's no need for a password.
            // Authorities (roles) associated with the user.
        //  Essentially this UsernamePasswordAuthenticationToken represents a successful authentication attempt.
        //  It's used by Spring Security for maintaining the user's authentication status.
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Extract the JWT from the Authorization header in an HTTP request. Expects an HTTP request as input (HttpServletRequest req):
    public String resolveToken(HttpServletRequest req) {

        // Retrieves the value of the Authorization header from the HTTP request:
        String bearerToken = req.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // Extract the token from the Authorization header by removing the first 7 characters (which is "Bearer "):
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            // Parse and verify a JWT using the secret key:
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("Expired or invalid JWT token", HttpStatus.UNAUTHORIZED);
        }
    }
}
