package com.packtpub.yummy.reverseproxy;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.integration.IntegrationMessageHeaderAccessor.CORRELATION_ID;

@Component
public class CorrelationIdZuulFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        String id = UUID.randomUUID().toString();
        RequestContext.getCurrentContext().addZuulRequestHeader(CORRELATION_ID, id);
        MDC.put(CORRELATION_ID, id);
        return null;
    }
}
