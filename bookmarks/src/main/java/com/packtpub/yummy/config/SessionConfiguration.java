package com.packtpub.yummy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@Profile("!test")
@EnableRedisHttpSession
public class SessionConfiguration {
}
