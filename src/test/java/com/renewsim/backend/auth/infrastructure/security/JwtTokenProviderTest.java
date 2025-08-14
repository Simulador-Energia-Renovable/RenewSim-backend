package com.renewsim.backend.auth.infrastructure.security;

import com.renewsim.backend.auth.domain.AuthenticatedUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.security.Key;
import java.security.SecureRandom;
import java.sql.Date;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static String randomBase64Key() {
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    @Test
    @DisplayName("generate/validate → valid token with roles and scopes")
    void generateAndValidate_ok() {
        String base64Key = randomBase64Key();
        Instant base = Instant.parse("2025-01-01T10:00:00Z");
        Clock clock = Clock.fixed(base, ZoneOffset.UTC);

        JwtTokenProvider provider = new JwtTokenProvider(base64Key, 3600L, 60L, clock);

        var user = new AuthenticatedUser("john", Set.of("USER"), Set.of("read"));
        String token = provider.generate(user);

        var res = provider.validate(token);

        assertThat(res).isPresent();
        assertThat(res.get().username()).isEqualTo("john");
        assertThat(res.get().roles()).containsExactly("USER");
        assertThat(res.get().scopes()).containsExactly("read");
    }

    @Test
    @DisplayName("validate → empty for malformed token")
    void validate_empty_malformed() {
        String base64Key = randomBase64Key();
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = new JwtTokenProvider(base64Key, 3600L, 60L, clock);

        assertThat(provider.validate("not.a.jwt")).isEmpty();
    }

    @Test
    @DisplayName("constructor/init → throws if secret < 32 bytes")
    void constructor_throws_shortSecret() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        assertThatThrownBy(() -> new JwtTokenProvider("short-key", 3600L, 60L, clock))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT secret too short");
    }

    @Test
    @DisplayName("expiresInSeconds → returns configured value")
    void expiresInSeconds_ok() {
        String base64Key = randomBase64Key();
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = new JwtTokenProvider(base64Key, 1234L, 60L, clock);

        assertThat(provider.expiresInSeconds()).isEqualTo(1234L);
    }

    @Test
    @DisplayName("validate → ok for subject-only token (no roles/scopes)")
    void validate_ok_subjectOnly() {
        String base64Key = randomBase64Key();
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = new JwtTokenProvider(base64Key, 3600L, 60L, clock);

        String token = provider.generate(new AuthenticatedUser("only-subject", null, null));
        var result = provider.validate(token);

        assertThat(result).isPresent();
        assertThat(result.get().username()).isEqualTo("only-subject");
        assertThat(result.get().roles()).isEmpty();
        assertThat(result.get().scopes()).isEmpty();
    }

    @Test
    @DisplayName("validate → OK within allowed clock skew after expiration")
    void validate_ok_withinSkew() {
        String base64Key = randomBase64Key();
        Instant base = Instant.parse("2025-01-01T10:00:00Z");

        long expSeconds = 30;
        long skewSeconds = 20;

        JwtTokenProvider signer = new JwtTokenProvider(base64Key, expSeconds, skewSeconds,
                Clock.fixed(base, ZoneOffset.UTC));

        String token = signer.generate(new AuthenticatedUser("john", Set.of("USER"), Set.of("read")));

        JwtTokenProvider validatorWithinSkew = new JwtTokenProvider(base64Key, expSeconds, skewSeconds,
                Clock.fixed(base.plusSeconds(45), ZoneOffset.UTC));

        assertThat(validatorWithinSkew.validate(token)).isPresent();
    }

    @Test
    @DisplayName("validate → empty when expired beyond allowed clock skew")
    void validate_empty_outsideSkew() {
        String base64Key = randomBase64Key();
        Instant base = Instant.parse("2025-01-01T10:00:00Z");

        long expSeconds = 30;
        long skewSeconds = 20;

        JwtTokenProvider signer = new JwtTokenProvider(base64Key, expSeconds, skewSeconds,
                Clock.fixed(base, ZoneOffset.UTC));

        String token = signer.generate(new AuthenticatedUser("john", Set.of("USER"), Set.of("read")));

        JwtTokenProvider validatorOutsideSkew = new JwtTokenProvider(base64Key, expSeconds, skewSeconds,
                Clock.fixed(base.plusSeconds(60), ZoneOffset.UTC));

        assertThat(validatorOutsideSkew.validate(token)).isEmpty();
    }

    @Test
    @DisplayName("validate → empty for token signed with different key (invalid signature)")
    void validate_empty_differentKey() {
        String base64Key = randomBase64Key();
        String attackerKey = randomBase64Key();
        Instant base = Instant.parse("2025-01-01T10:00:00Z");

        JwtTokenProvider validator = new JwtTokenProvider(base64Key, 60L, 0L,
                Clock.fixed(base, ZoneOffset.UTC));

        Key attackerSigningKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(attackerKey));
        String forged = Jwts.builder()
                .setSubject("john")
                .setIssuedAt(Date.from(base))
                .setExpiration(Date.from(base.plusSeconds(60)))
                .signWith(attackerSigningKey, SignatureAlgorithm.HS256)
                .compact();

        assertThat(validator.validate(forged)).isEmpty();
    }

    @Test
    @DisplayName("validate → empty for token using a different alg (HS384)")
    void validate_empty_differentAlg() {
        String base64Key = randomBase64Key();
        Instant base = Instant.parse("2025-01-01T10:00:00Z");

        JwtTokenProvider validator = new JwtTokenProvider(base64Key, 60L, 0L,
                Clock.fixed(base, ZoneOffset.UTC));
        Key hs384Key = Keys.secretKeyFor(SignatureAlgorithm.HS384);
        String hs384Token = Jwts.builder()
                .setSubject("john")
                .setIssuedAt(Date.from(base))
                .setExpiration(Date.from(base.plusSeconds(60)))
                .signWith(hs384Key, SignatureAlgorithm.HS384)
                .compact();
        assertThat(validator.validate(hs384Token)).isEmpty();
    }

    @Test
    @DisplayName("toStringSet → devuelve vacío cuando claim no es colección")
    void toStringSet_ShouldReturnEmpty_WhenClaimNotCollection() throws Exception {
        Method m = JwtTokenProvider.class.getDeclaredMethod("toStringSet", Object.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        Set<String> result = (Set<String>) m.invoke(null, "single-role");
        assertThat(result).isEmpty();
    }

}
