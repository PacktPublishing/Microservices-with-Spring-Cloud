package com.packtpub.yummy.reverseproxy;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

@Component
public class ZuulLoginFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        int statusCode = RequestContext.getCurrentContext().getResponseStatusCode();
        return statusCode == 401 || statusCode == 403;
    }
    @Override
    public Object run() throws ZuulException {
        try {
            RequestContext.getCurrentContext()
                    .getResponse()
                    .sendRedirect("/user/login");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
