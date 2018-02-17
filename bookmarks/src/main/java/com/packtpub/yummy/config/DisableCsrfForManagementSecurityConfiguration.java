package com.packtpub.yummy.config;

import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Configuration
@Order(99)
public class DisableCsrfForManagementSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static RequestMatcher allOf(RequestMatcher... requestMatchers) {
        return new AndRequestMatcher(requestMatchers);
    }

    private static RequestMatcher not(RequestMatcher requestMatcher) {
        return new NegatedRequestMatcher(requestMatcher);
    }

    private final ManagementServerProperties managementServerProperties;

    public DisableCsrfForManagementSecurityConfiguration(ManagementServerProperties managementServerProperties) {
        this.managementServerProperties = Objects.requireNonNull(managementServerProperties);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().requireCsrfProtectionMatcher(
                allOf(CsrfFilter.DEFAULT_CSRF_MATCHER, not(accessingManagementServlet())));
    }

    private RequestMatcher accessingManagementServlet() {
        return httpServletRequest ->
                isAccessingOnManagementPort(httpServletRequest)
                        || isAccessingWithContextPath(httpServletRequest);
    }

    private boolean isAccessingOnManagementPort(HttpServletRequest httpServletRequest) {
        return managementServerProperties.getPort() != null &&
                httpServletRequest.getLocalPort() == managementServerProperties.getPort();
    }

    private boolean isAccessingWithContextPath(HttpServletRequest httpServletRequest) {
        return managementServerProperties.getPort() == null &&
                managementServerProperties.getContextPath() != null &&
                httpServletRequest.getServletPath().startsWith(managementServerProperties.getContextPath());
    }
}