# ADR-004 — JWT Review-Driven Robustness Refinements

**ID:** ADR-004  
**Date:** 2025-08-14  
**Status:** Accepted  
**Version:** v1.0

---

## 1. Title
JWT filter/provider refinements: explicit Optional flow, constructor visibility, and strict claim parsing.

---

## 2. Context
Tras una revisión de PR (incluyendo sugerencias automáticas), se identificaron tres puntos de mejora en el módulo de autenticación JWT:

1) **Flujo con Optional en el filtro**: `ifPresentOrElse` es funcional pero menos legible para logging y futuras métricas.  
2) **Constructor sin argumentos en el provider**: un `@Component` con constructor público puede permitir instanciación sin configuración de Spring, provocando estados inválidos.  
3) **Parsing de claims**: convertir cualquier valor no colección a `Set<String>` puede introducir autoridades inesperadas si el claim no sigue el contrato.

Estas mejoras no cambian comportamiento público, pero incrementan robustez, legibilidad y evitan configuraciones incorrectas.

---

## 3. Decision
- **JwtAuthenticationFilter**
  - Sustituir `ifPresentOrElse` por flujo explícito:
    - `Optional<AuthenticatedUser> validated = tokenProvider.validate(token);`
    - `validated.ifPresent(user -> setAuthentication(...))`
    - `if (validated.isEmpty()) log.warn(...)`
  - Mantener:
    - `finally { chain.doFilter(...) }`
    - Parsing `Bearer` **case-insensitive** + `trim`.

- **JwtTokenProvider**
  - Cambiar el **constructor sin argumentos** a **package-private** para evitar instanciación accidental fuera de Spring.
  - **Parsing estricto de claims**: si `roles/scopes` no son `Collection`, devolver **`Collections.emptySet()`** en lugar de interpretar valores escalares automáticamente.

- **Tests**
  - Añadir test de **claim no colección** → set vacío.
  - Mantener cobertura existente: **subject-only**, **expired/outside skew**, **within skew**, **algoritmo forzado** (HS256).

---

## 4. Rationale
- **Legibilidad y extensibilidad**: El flujo explícito con `Optional` facilita añadir métricas y es más claro para el lector.  
- **Seguridad de configuración**: Reducir visibilidad del constructor por defecto previene usos incorrectos fuera del contenedor IoC.  
- **Contrato claro de claims**: Evitar interpretar implícitamente datos no conformes reduce sorpresas (principio de menor sorpresa) y endurece la superficie de entrada.

---

## 5. Consequences
✅ Mejor legibilidad y capacidad de extensión en el filtro (logs/métricas).  
✅ Menor probabilidad de instancias mal configuradas del provider.  
✅ Claims con contrato más estricto (menos riesgo de autoridades inesperadas).

⚠️ Si algún emisor externo enviaba `roles/scopes` como string escalar en lugar de lista, deberá ajustarse o añadirse una conversión explícita en una capa de compatibilidad.

---

## 6. Alternatives Considered
- Mantener `ifPresentOrElse`: válido pero menos claro para logging y futura instrumentación.
- Aceptar claims escalares y convertirlos a `Set`: más flexible pero más propenso a errores silenciosos.
- Constructor público: más “amigable” pero inseguro para un `@Component` que depende de configuración inyectada.

---

## 7. References
- ADR-0001 — JWT Security Hardening  
- ADR-002 — Auth error semantics (401/409)  
- PR: *JWT robustness review* (número por definir)
