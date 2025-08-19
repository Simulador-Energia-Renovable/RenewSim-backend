package com.renewsim.backend.shared.observability;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

class AuthAuditLoggerTest {

    @Test
    void shouldEmitSingleWarnWithoutSecrets() {
        Logger logger = (Logger) LoggerFactory.getLogger("AUTH_AUDIT");
        var app = new ListAppender<ILoggingEvent>();
        app.start();
        logger.addAppender(app);

        MDC.put(CorrelationIdFilter.MDC_KEY, "cid-123");

        try {
            AuthAuditLogger.warnAuthFailure("INVALID_CREDENTIALS", "203.0.113.10", "alice");

            assertThat(app.list).hasSize(1);
            var evt = app.list.get(0);
            assertThat(evt.getLevel()).isEqualTo(Level.WARN);
            var msg = evt.getFormattedMessage();
            assertThat(msg).contains("auth_failure",
                    "reason=INVALID_CREDENTIALS",
                    "clientIp=203.0.113.10",
                    "username=alice",
                    "correlationId=cid-123");
            assertThat(msg).doesNotContain("password", "token", "Authorization", "Cookie");
        } finally {
            logger.detachAppender(app);
            MDC.clear();
        }
    }
}

