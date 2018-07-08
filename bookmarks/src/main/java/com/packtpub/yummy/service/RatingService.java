package com.packtpub.yummy.service;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static java.util.Arrays.stream;

@Service
@Slf4j
public class RatingService {
    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;
    @Autowired
    HttpServletRequest request;

    @SneakyThrows
    @Retryable(maxAttempts = 5, backoff = @Backoff(multiplier = 2, random = true))
    public void removeRatings(UUID id) {
        RequestEntity entity = RequestEntity
                .delete(new UriTemplate("http://ratings/votes/bookmark/{id}")
                        .expand(id))
                .header("COOKIE", "SESSION=" + getCookieValue("SESSION"))
                .build();
        new HystrixCommand<Void>(
                HystrixCommand.Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey("ratings"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("removeRatings"))
        ) {
            @Override
            protected Void run() throws Exception {
                restTemplate.exchange(entity, Void.class);
                return null;
            }

            @Override
            protected Void getFallback() {
                Exception exception = executionResult.getException();
                if (exception == null) exception = executionResult.getExecutionException();
                if (exception == null) {
                    throw new RuntimeException("Oh no! result: " + executionResult);
                } else {
                    log.info("Oh no! {}", exception.getMessage());
                    throw new RuntimeException(exception);
                }
            }
        }.execute();
    }

    private String getCookieValue(String name) {
        return stream(request.getCookies())
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("");
    }
}
