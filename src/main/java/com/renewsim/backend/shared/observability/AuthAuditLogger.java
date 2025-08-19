package com.renewsim.backend.shared.observability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthAuditLogger {

    private static final Logger log = LoggerFactory.getLogger("AUTH_AUDIT");

    private AuthAuditLogger() {}

    public static void warnAuthFailure(String reason, String clientIp, String usernameOrNull) {
        log.warn("auth_failure reason={} clientIp={} username={} correlationId={}",
                safe(reason),
                safe(clientIp),
                safeUsername(usernameOrNull),
                CorrelationIdMdc.currentCorrelationId());
    }

    private static String safe(String v) {
        return v == null ? "-" : v.replaceAll("[\\r\\n\\t]", "_");
    }

    private static String safeUsername(String v) {
        if (v == null || v.isBlank()) return "-";
        return v.replaceAll("\\s+", "");
    }
    static final class CorrelationIdMdc {
        static String currentCorrelationId() {
            String c = org.slf4j.MDC.get(CorrelationIdFilter.MDC_KEY);
            return c == null ? "-" : c;
        }
    }
}
