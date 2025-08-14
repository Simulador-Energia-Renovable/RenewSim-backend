# 📝 ADR-0001 — JWT Security Hardening

**ID:** ADR-0001  
**Date:** 2025-08-14  
**Status:** Accepted  
**Version:** v1.0

---

## 1. Title  
Enforce HS256 algorithm, robust token parsing, and filter improvements for JWT authentication.

---

## 2. Context  
El sistema de autenticación usaba JWT con una implementación base que:  
- No controlaba estrictamente el algoritmo aceptado al validar.  
- Tenía logs de parsing en nivel **DEBUG**, lo que dificultaba la observabilidad en producción.  
- No contemplaba casos como `Bearer` con espacios o casing distinto.  
- No tenía pruebas para tokens expirados o tokens sin claims adicionales (solo `sub`).  
- No gestionaba de forma explícita el clock skew permitido en validación.

---

## 3. Decision  
- Usar **HS256** como único algoritmo permitido para firmar y validar JWT.  
- Configurar `JwtParserBuilder` con `setAllowedClockSkewSeconds(clockSkewSeconds)`.  
- Subir nivel de log de errores de parsing a **WARN** en `JwtAuthenticationFilter`.  
- Soportar `Bearer` case-insensitive y con espacios adicionales (`trim`).  
- Añadir pruebas para:
  - Token expirado.  
  - Token válido sin claims adicionales.  
  - Token con expiración cercana dentro del `clockSkew`.

---

## 4. Rationale  
- **Seguridad:** Evitar ataques como `alg:none` o uso de algoritmos no seguros.  
- **Consistencia:** Garantizar que todos los tokens tengan un algoritmo y tamaño de clave correctos según RFC 7518.  
- **Observabilidad:** Logs a nivel **WARN** facilitan detección de problemas en producción sin inundar con ruido.  
- **Resiliencia:** El filtro maneja casos atípicos sin romper la autenticación de tokens válidos.

---

## 5. Consequences  
✅ Tokens seguros y consistentes con HS256.  
✅ Mejor trazabilidad de errores en producción.  
✅ Mayor tolerancia a variaciones de encabezado `Bearer`.  
⚠️ Migrar a otro algoritmo en el futuro requerirá rotación de claves y actualización de validadores.

---

## 6. Alternatives Considered  
- **RS256:** Más seguro en entornos distribuidos, pero innecesario para el escenario actual y más complejo de gestionar.  
- **Soportar múltiples algoritmos:** Mayor flexibilidad, pero incrementa riesgos de configuración insegura.  
- **Sin clock skew:** Rechazaría tokens válidos si hay pequeñas diferencias de tiempo entre sistemas.

---

## 7. References  
- [RFC 7518 — JSON Web Algorithms](https://datatracker.ietf.org/doc/html/rfc7518)  
- [OWASP JWT Security Guidelines](https://owasp.org/www-project-cheat-sheets/cheatsheets/JSON_Web_Token_Cheat_Sheet.html)  
- PR #XX — "JWT Security Hardening"
