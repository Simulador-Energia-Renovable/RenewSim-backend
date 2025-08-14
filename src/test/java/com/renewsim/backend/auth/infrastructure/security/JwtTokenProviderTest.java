package com.renewsim.backend.auth.infrastructure.security;

import com.renewsim.backend.auth.domain.AuthenticatedUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private static String randomBase64Key() {
        byte[] keyBytes = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    @Test
    @DisplayName("generate/validate → token válido con roles y scopes")
    void generateAndValidate_ShouldWorkCorrectly() {
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
    @DisplayName("validate → vacío para token inválido")
    void validate_ShouldReturnEmptyForInvalidToken() {
        String base64Key = randomBase64Key();
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = new JwtTokenProvider(base64Key, 3600L, 60L, clock);

        assertThat(provider.validate("not.a.jwt")).isEmpty();
    }

    @Test
    @DisplayName("constructor/init → lanza si el secreto es corto (<32 bytes)")
    void constructor_ShouldThrowIfSecretTooShort() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        assertThatThrownBy(() -> new JwtTokenProvider("short-key", 3600L, 60L, clock))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT secret too short");
    }

    @Test
    @DisplayName("expiresInSeconds → devuelve el valor configurado")
    void expiresInSeconds_ShouldReturnConfiguredValue() {
        String base64Key = randomBase64Key();
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = new JwtTokenProvider(base64Key, 1234L, 60L, clock);

        assertThat(provider.expiresInSeconds()).isEqualTo(1234L);
    }

    @Test
    @DisplayName("toStringSet (privado) → convierte Collection a Set<String>")
    @SuppressWarnings("unchecked")
    void toStringSet_ShouldConvertCollectionToSet() throws Exception {
        Method m = JwtTokenProvider.class.getDeclaredMethod("toStringSet", Object.class);
        m.setAccessible(true);

        Set<String> result = (Set<String>) m.invoke(null, List.of("ROLE_USER", "ROLE_ADMIN"));
        assertThat(result).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("toStringSet (privado) → devuelve singleton cuando claim es escalar (no colección)")
    @SuppressWarnings("unchecked")
    void toStringSet_ShouldReturnSingleton_WhenNotCollection() throws Exception {
        Method m = JwtTokenProvider.class.getDeclaredMethod("toStringSet", Object.class);
        m.setAccessible(true);

        Set<String> result = (Set<String>) m.invoke(null, "not-a-collection");
        assertThat(result).containsExactly("not-a-collection");
    }

    @Test
    @DisplayName("constructor/init → usa UTF-8 cuando el secret NO es Base64 (pero suficientemente largo)")
    void constructor_ShouldDecodeSecretAsUtf8_WhenBase64Invalid() {
        String longUtf8 = "this-is-not-base64-but-it-is-long-enough-32+bytes-string!!!";
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);

        new JwtTokenProvider(longUtf8, 3600L, 60L, clock);
    }

    @Test
    @DisplayName("constructor/init → acepta exactamente 32 bytes")
    void constructor_ShouldAcceptSecretWithExactly32Bytes() {
        byte[] exactly32 = new byte[32];
        Arrays.fill(exactly32, (byte) 65); // 'A'
        String base64 = Base64.getEncoder().encodeToString(exactly32);
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);

        new JwtTokenProvider(base64, 3600L, 60L, clock);
    }

    @Test
    @DisplayName("generate → funciona sin roles ni scopes (subject-only)")
    void generate_ShouldWorkWithoutRolesOrScopes() {
        String base64Key = randomBase64Key();
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = new JwtTokenProvider(base64Key, 3600L, 60L, clock);

        var user = new AuthenticatedUser("no-claims", Set.of(), Set.of());
        String token = provider.generate(user);
        assertThat(token).isNotBlank();

        var parsed = provider.validate(token);
        assertThat(parsed).isPresent();
        assertThat(parsed.get().username()).isEqualTo("no-claims");
        assertThat(parsed.get().roles()).isEmpty();
        assertThat(parsed.get().scopes()).isEmpty();
    }

    @Test
    @DisplayName("generate → funciona cuando roles es null y scopes tiene datos")
    void generate_ShouldWork_WhenRolesNullAndScopesPresent() {
        String base64Key = randomBase64Key();
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = new JwtTokenProvider(base64Key, 3600L, 60L, clock);

        var user = new AuthenticatedUser("null-roles", null, Set.of("read"));
        String token = provider.generate(user);

        var parsed = provider.validate(token);
        assertThat(parsed).isPresent();
        assertThat(parsed.get().username()).isEqualTo("null-roles");
        assertThat(parsed.get().roles()).isEmpty();
        assertThat(parsed.get().scopes()).containsExactly("read");
    }

    @Test
    @DisplayName("generate → funciona cuando scopes es null y roles tiene datos")
    void generate_ShouldWork_WhenScopesNullAndRolesPresent() {
        String base64Key = randomBase64Key();
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        JwtTokenProvider provider = new JwtTokenProvider(base64Key, 3600L, 60L, clock);

        var user = new AuthenticatedUser("null-scopes", Set.of("USER"), null);
        String token = provider.generate(user);

        var parsed = provider.validate(token);
        assertThat(parsed).isPresent();
        assertThat(parsed.get().username()).isEqualTo("null-scopes");
        assertThat(parsed.get().roles()).containsExactly("USER");
        assertThat(parsed.get().scopes()).isEmpty();
    }

    @Test
    @DisplayName("validate → OK dentro del allowed clock skew tras la expiración")
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
    @DisplayName("validate → vacío cuando expira fuera del allowed clock skew")
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
}

