package com.packtpub.yummy.tracing;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static org.springframework.integration.IntegrationMessageHeaderAccessor.CORRELATION_ID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CorrelationIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String correlationId = ofNullable(request.getHeader(CORRELATION_ID)).orElse(UUID.randomUUID().toString());
        MDC.put(CORRELATION_ID, correlationId);
        log.info("Got request! ID {}", request.getHeader(CORRELATION_ID));
        filterChain.doFilter(request, response);
    }
}
