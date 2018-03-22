package com.packtpub.yummy.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

@Configuration
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final DataSource dataSource;
    private final ManagementServerProperties managementServerProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll()
                .and()
                .logout().permitAll();
        http.csrf().requireCsrfProtectionMatcher(
                allOf(CsrfFilter.DEFAULT_CSRF_MATCHER, not(accessingManagementServlet())));
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
        builder.jdbcAuthentication().dataSource(dataSource)
        .authoritiesByUsernameQuery("Select * from user_roles where username=?");
    }

    private static RequestMatcher allOf(RequestMatcher... requestMatchers) {
        return new AndRequestMatcher(requestMatchers);
    }

    private static RequestMatcher not(RequestMatcher requestMatcher) {
        return new NegatedRequestMatcher(requestMatcher);
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
