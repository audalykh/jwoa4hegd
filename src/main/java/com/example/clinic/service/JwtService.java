package com.example.clinic.service;

import com.example.clinic.config.JwtConfigProperties;
import com.example.clinic.model.Person;
import com.example.clinic.security.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.security.StandardSecureDigestAlgorithms;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String ROLE = "role";
    private static final String ACTOR_ID = "actorId";

    private final JwtConfigProperties jwtConfigProperties;

    /**
     * Extract username from token
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the role from the token
     */
    public Role extractRole(String token) {
        return Role.valueOf(extractClaim(token, claims -> claims.get(ROLE, String.class)));
    }

    public String generateToken(Person person) {
        Map<String, Object> claims = Map.of(
                ACTOR_ID, person.getId(),
                ROLE, Role.fromPerson(person).name()
        );
        return generateToken(claims, person);
    }

    /**
     * Check if the token is valid
     */
    public boolean isTokenValid(String token, Person person) {
        final String userName = extractUserName(token);
        return userName.equals(person.getEmail()) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, Person person) {
        long expirationTimeMillis = TimeUnit.MINUTES.toMillis(jwtConfigProperties.getExpirationTimeMinutes());
        return Jwts.builder().claims(extraClaims).subject(person.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(getSigningKey(), StandardSecureDigestAlgorithms.findBySigningKey(getSigningKey())).compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfigProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

