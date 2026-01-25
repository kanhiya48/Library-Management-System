
package com.tcs.Library.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AuthUtils {

    @Value("${jwt.secret}")
    private String secret;

    // @Value("${jwt.access-token-exp-min}")
    // private long accessExpMin;

    // @Value("${jwt.refresh-token-exp-days}")
    // private long refreshExpDays;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64
                .decode(java.util.Base64.getEncoder().encodeToString(secret.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String username, String customerName, String userPublicId,
            Collection<? extends GrantedAuthority> authority) {

        List<String> roles = authority.stream().filter(Objects::nonNull)
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull).map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .distinct().collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("customerName", customerName);
        claims.put("userPublicId", userPublicId);

        Instant now = Instant.now();
        return Jwts.builder().subject(username).header().empty().add("type", "JWT").and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(10, ChronoUnit.MINUTES))).claims(claims)
                .signWith(getSigningKey()).compact();
    }

    // public String generateRefreshToken(User user) {
    // Instant now = Instant.now();
    // return Jwts.builder().subject(user.getUsername()).issuedAt(Date.from(now))
    // .expiration(Date.from(now.plus(refreshExpDays, ChronoUnit.DAYS)))
    // .claim("type", "refresh").signWith(getSigningKey()).compact();
    // }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object type = claims.get("type");
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token)
                .getPayload();
    }
}
