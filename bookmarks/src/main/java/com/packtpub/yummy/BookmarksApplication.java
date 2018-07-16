package com.packtpub.yummy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.Arrays;

@SpringBootApplication
@EnableCircuitBreaker
@EnableRetry
@Slf4j
public class BookmarksApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BookmarksApplication.class);
        if (noActiveProfiles(args)) {
            app.setAdditionalProfiles("dev", System.getProperty("user.name"));
        }
        ConfigurableApplicationContext context = app.run(args);

        log.info("");
        log.info("#######################");
        log.info("##### Initialized! ####");
        log.info("#######################");
        log.info(" go to: http://localhost:8080");
    }

    private static boolean noActiveProfiles(String[] args) {
        return new StandardServletEnvironment().getActiveProfiles().length == 0
                && Arrays.stream(args)
                .noneMatch(param -> param.startsWith("--spring.profiles.active"));
    }

}
