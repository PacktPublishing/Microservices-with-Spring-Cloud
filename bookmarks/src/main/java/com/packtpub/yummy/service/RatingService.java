package com.packtpub.yummy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static java.util.Arrays.stream;

@Service
public class RatingService {
    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;
    @Autowired
    HttpServletRequest request;

    public void removeRatings(UUID id) {
        RequestEntity entity = RequestEntity
                .delete(new UriTemplate("http://ratings/votes/bookmark/{id}")
                        .expand(id))
                .header("COOKIE", "SESSION=" + getCookieValue("SESSION"))
                .build();
        restTemplate.exchange(entity, Void.class);
    }

    private String getCookieValue(String name) {
        return stream(request.getCookies())
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("");
    }
}
