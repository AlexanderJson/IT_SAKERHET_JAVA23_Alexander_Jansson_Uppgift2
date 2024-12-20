package org.example.inl.Security.JWT;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwTUtil {

    private final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // hämtar data till payload (userid, username)
    public String generateToken(String username, Long userId){
        Map<String, Object> claims = new HashMap<>();
        // lägger till userID till claims
        claims.put("userId", userId);
        System.out.println("Generated Token: " + tokenBuilder(claims, username));
        return tokenBuilder(claims, username);
    }

    //skickar payload till builder, genererar header + payload
    // + tar vår genererade nyckel -> (header+payload+key) = input till HS512 algoritmen
    private String tokenBuilder(Map<String, Object> claims,String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // 10 timmar för testning
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(KEY)
                .compact();
    }

    public Boolean validateToken(String token,String username){
        final String extractedUsername = extractedUsername(token);
        return extractedUsername.equals(username)&& !isTokenExpired(token);
    }

    public String extractedUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public Long extractId(String token){
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }
}
