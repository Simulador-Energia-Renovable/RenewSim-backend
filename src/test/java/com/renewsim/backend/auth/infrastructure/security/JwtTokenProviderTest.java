package com.renewsim.backend.auth.infrastructure.security;

import com.renewsim.backend.auth.domain.AuthenticatedUser;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() throws Exception {
        provider = new JwtTokenProvider();

        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);

        setField(provider, "secret", base64Key);
        setField(provider, "expirationSeconds", 3600L);
        setField(provider, "clockSkewSeconds", 60L);

        provider.init();
    }

    @Test
    @DisplayName("generate/validate → token válido con roles y scopes")
    void generateAndValidate_ShouldWorkCorrectly() {
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
        assertThat(provider.validate("not.a.jwt")).isEmpty();
    }

    @Test
    @DisplayName("init → lanza si el secreto es corto (<32 bytes)")
    void init_ShouldThrowIfSecretTooShort() throws Exception {
        var p = new JwtTokenProvider();
        setField(p, "secret", "short-key");
        setField(p, "expirationSeconds", 3600L);
        setField(p, "clockSkewSeconds", 60L);

        assertThatThrownBy(p::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT secret too short");
    }

    @Test
    @DisplayName("expiresInSeconds → devuelve el valor configurado")
    void expiresInSeconds_ShouldReturnConfiguredValue() throws Exception {
        setField(provider, "expirationSeconds", 1234L);
        assertThat(provider.expiresInSeconds()).isEqualTo(1234L);
    }

    @Test
    @DisplayName("toStringSet → convierte Collection a Set<String>")
    @SuppressWarnings("unchecked")
    void toStringSet_ShouldConvertCollectionToSet() throws Exception {
        Method m = JwtTokenProvider.class.getDeclaredMethod("toStringSet", Object.class);
        m.setAccessible(true);

        Set<String> result = (Set<String>) m.invoke(null, List.of("ROLE_USER", "ROLE_ADMIN"));
        assertThat(result).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("toStringSet → Set vacío cuando no es Collection")
    @SuppressWarnings("unchecked")
    void toStringSet_ShouldReturnEmptySet_WhenNotCollection() throws Exception {
        Method m = JwtTokenProvider.class.getDeclaredMethod("toStringSet", Object.class);
        m.setAccessible(true);

        Set<String> result = (Set<String>) m.invoke(null, "not-a-collection");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("init → usa UTF-8 cuando el secret NO es Base64 (pero suficientemente largo)")
    void init_ShouldDecodeSecretAsUtf8_WhenBase64Invalid() throws Exception {
        var p = new JwtTokenProvider();
        setField(p, "secret", "this-is-not-base64-but-is-long-enough-32-bytes!!");
        setField(p, "expirationSeconds", 3600L);
        setField(p, "clockSkewSeconds", 60L);

        p.init();
    }

    @Test
    @DisplayName("init → acepta exactamente 32 bytes")
    void init_ShouldAcceptSecretWithExactly32Bytes() throws Exception {
        var p = new JwtTokenProvider();
        byte[] exactly32 = new byte[32];
        Arrays.fill(exactly32, (byte) 65); 
        String base64 = Base64.getEncoder().encodeToString(exactly32);

        setField(p, "secret", base64);
        setField(p, "expirationSeconds", 3600L);
        setField(p, "clockSkewSeconds", 60L);

        p.init();
    }

    @Test
    @DisplayName("generate → funciona sin roles ni scopes (no añade claims opcionales)")
    void generate_ShouldWorkWithoutRolesOrScopes() {
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
        var user = new AuthenticatedUser("null-scopes", Set.of("USER"), null);
        String token = provider.generate(user);

        var parsed = provider.validate(token);
        assertThat(parsed).isPresent();
        assertThat(parsed.get().username()).isEqualTo("null-scopes");
        assertThat(parsed.get().roles()).containsExactly("USER");
        assertThat(parsed.get().scopes()).isEmpty();
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }
}
