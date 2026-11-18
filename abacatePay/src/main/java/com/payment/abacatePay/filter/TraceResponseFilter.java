package com.payment.abacatePay.filter;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TraceResponseFilter
        extends OncePerRequestFilter {

    private static final String TRACE_HEADER =
            "X-Trace-Id";

    private static final String SPAN_HEADER =
            "X-Span-Id";

    private final Tracer tracer;

    public TraceResponseFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        filterChain.doFilter(request, response);

        Span currentSpan = tracer.currentSpan();

        if (currentSpan == null) {
            return;
        }

        response.setHeader(
                TRACE_HEADER,
                currentSpan.context().traceId()
        );

        response.setHeader(
                SPAN_HEADER,
                currentSpan.context().spanId()
        );
    }
}