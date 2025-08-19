# Observability — Structured Logging for Auth Failures

## Goals
- Emit a single `WARN` structured log record for any login failure.
- Never log secrets (passwords, tokens, cookies).
- Support correlation via `X-Correlation-Id` across requests and logs.

## Correlation
- Incoming requests may provide `X-Correlation-Id`.
- If absent, the backend generates a UUID.
- The correlation id is stored in MDC as `correlationId` and echoed back in the response header.

## Log Fields
- `reason`: normalized failure cause (e.g., `INVALID_CREDENTIALS`, `ACCOUNT_LOCKED`, `RATE_LIMIT`, `AUTH_FAILURE`).
- `clientIp`: client public IP best-effort, honoring `X-Forwarded-For`.
- `username`: provided username if present (never log password).
- `correlationId`: taken from MDC to track the request.

## Security
- Do NOT log `password`, `token`, `refreshToken`, `Authorization`, `Cookie` or any secret.
- All logs are sanitized against CR/LF injection.

## Acceptance Criteria
- GIVEN a failed login
- WHEN the controller handles the exception
- THEN a single WARN log line is emitted with the fields above, and the response still returns `X-Correlation-Id`.

## Testing
- Unit test validates a single WARN event contains `reason`, `clientIp`, `username`, `correlationId` and excludes secrets.
- Filter test verifies MDC lifecycle and response header behavior.
