package com.packtpub.yummy.users.config;

import com.packtpub.yummy.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitialization implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(DataInitialization.class);
    @Autowired
    UserService userService;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!userService.hasUser("admin")) {
            userService.addUser(new User("admin", "password",
                    Arrays.asList(
                            new SimpleGrantedAuthority("ROLE_ADMIN"),
                            new SimpleGrantedAuthority("ROLE_USER")
                    )));
        }
        if (!userService.hasUser("user")) {
            userService.addUser(new User("user", "password",
                    Arrays.asList(
                            new SimpleGrantedAuthority("ROLE_USER")
                    )));
        }
    }
}