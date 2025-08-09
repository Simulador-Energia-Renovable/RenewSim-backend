package com.renewsim.backend.auth.infrastructure.security;

import com.renewsim.backend.auth.application.port.out.TokenProvider;
import com.renewsim.backend.auth.domain.AuthenticatedUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.util.*;

@Component
public class JwtTokenProvider implements TokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationSeconds:3600}")
    private long expirationSeconds;

    @Value("${jwt.clockSkewSeconds:60}")
    private long clockSkewSeconds;

    private Key key;
    private final Clock clock = Clock.systemUTC();

    @PostConstruct
    void init() {
        byte[] raw;
        try {
            raw = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException ex) {
            raw = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (raw.length < 32) throw new IllegalStateException("JWT secret too short (min 32 bytes / 256 bits).");
        key = Keys.hmacShaKeyFor(raw);
    }

    @Override
    public String generate(AuthenticatedUser user) {
        Map<String, Object> claims = new HashMap<>();
        if (user.roles() != null && !user.roles().isEmpty()) claims.put("roles", user.roles());
        if (user.scopes() != null && !user.scopes().isEmpty()) claims.put("scopes", user.scopes());

        Date now = Date.from(clock.instant());
        Date exp = Date.from(clock.instant().plusSeconds(expirationSeconds));

        return Jwts.builder()
                .setSubject(user.username())
                .setIssuedAt(now)
                .setExpiration(exp)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Optional<AuthenticatedUser> validate(String token) {
        try {
            var jws = Jwts.parserBuilder()
                    .setAllowedClockSkewSeconds(clockSkewSeconds)
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            Claims c = jws.getBody();
            String subject = c.getSubject();
            Set<String> roles = toStringSet(c.get("roles"));
            Set<String> scopes = toStringSet(c.get("scopes"));

            return Optional.of(new AuthenticatedUser(subject, roles, scopes));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    @Override
    public long expiresInSeconds() {
        return expirationSeconds;
    }

    private static Set<String> toStringSet(Object obj) {
        if (obj instanceof Collection<?> col) {
            Set<String> out = new HashSet<>();
            for (Object o : col) out.add(String.valueOf(o));
            return out;
        }
        return Set.of();
    }
}
