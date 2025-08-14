package com.renewsim.backend.auth.infrastructure.security;

import com.renewsim.backend.auth.application.port.out.TokenProvider;
import com.renewsim.backend.auth.domain.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

@Component
public class JwtTokenProvider implements TokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationSeconds:3600}")
    private long expirationSeconds;

    @Value("${jwt.clockSkewSeconds:60}")
    private long clockSkewSeconds;

    private Key key;

    private Clock clock = Clock.systemUTC();

    private io.jsonwebtoken.Clock jjwtClock = () -> Date.from(clock.instant());

    JwtTokenProvider(String secret, long expirationSeconds, long clockSkewSeconds, Clock clock) {
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
        this.clockSkewSeconds = clockSkewSeconds;
        this.clock = (clock != null) ? clock : Clock.systemUTC();
        this.jjwtClock = () -> Date.from(this.clock.instant());
        init(); 
    }

    public JwtTokenProvider() {
    }

    @PostConstruct
    void init() {
        byte[] raw;
        try {
            raw = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException ex) {
            raw = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (raw.length < 32) {
            throw new IllegalStateException(
                "JWT secret too short: got " + raw.length + " bytes, expected at least 32 bytes (256 bits)."
            );
        }
        this.key = Keys.hmacShaKeyFor(raw);
    }

    @Override
    public String generate(AuthenticatedUser user) {
        var claims = new HashMap<String, Object>(4);
        if (user.roles() != null && !user.roles().isEmpty()) {
            claims.put("roles", user.roles());
        }
        if (user.scopes() != null && !user.scopes().isEmpty()) {
            claims.put("scopes", user.scopes());
        }

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
            Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .setClock(jjwtClock)
                .build()
                .parseClaimsJws(token);

            JwsHeader<?> header = jws.getHeader();
            if (!SignatureAlgorithm.HS256.getValue().equals(header.getAlgorithm())) {
                return Optional.empty();
            }

            Claims c = jws.getBody();
            String subject = c.getSubject();
            if (subject == null || subject.isBlank()) {
                return Optional.empty();
            }

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

    private static Set<String> toStringSet(Object claim) {
        if (claim == null) return Collections.emptySet();
        if (claim instanceof Collection<?> col) {
            Set<String> out = new HashSet<>();
            for (Object o : col) {
                if (o != null) out.add(String.valueOf(o));
            }
            return out;
        }
        return Set.of(String.valueOf(claim));
    }
}

