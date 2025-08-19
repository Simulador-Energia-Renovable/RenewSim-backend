package com.renewsim.backend.shared.observability;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void shouldPropagateIncomingHeader() throws Exception {
        var req = new MockHttpServletRequest();
        var res = new MockHttpServletResponse();
        req.addHeader(CorrelationIdFilter.HEADER, "abc-123");

        filter.doFilter(req, res, new MockFilterChain());

        assertThat(res.getHeader(CorrelationIdFilter.HEADER)).isEqualTo("abc-123");
        assertThat(org.slf4j.MDC.get(CorrelationIdFilter.MDC_KEY)).isNull();
    }

    @Test
    void shouldGenerateWhenMissing() throws Exception {
        var req = new MockHttpServletRequest();
        var res = new MockHttpServletResponse();

        filter.doFilter(req, res, new MockFilterChain());

        assertThat(res.getHeader(CorrelationIdFilter.HEADER)).isNotBlank();
    }
}

