package com.blog.auth;

import com.blog.core.User;
import com.blog.db.UserDAO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Optional;

public class JwtService {
    private final String secretKey;
    private final long expirationTime;
    private  final UserDAO userDAO;
    public JwtService(String secretKey, long expirationTime,UserDAO userDAO) {
        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
        this.userDAO=userDAO;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(user.getName())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Optional<User> verifyToken(String token) {
        try {
            // Verify and decode the JWT token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            Optional<User> user = userDAO.findByUsername(username);

            return user;
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    public Claims decodeToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
