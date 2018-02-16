package com.packtpub.yummy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.StandardServletEnvironment;

@SpringBootApplication
public class BookmarksApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BookmarksApplication.class);
        if(new StandardServletEnvironment().getActiveProfiles().length==0){
            app.setAdditionalProfiles("dev", System.getProperty("user.name"));
        }
        ConfigurableApplicationContext context = app.run(args);

        System.out.println();
        System.out.println("#######################");
        System.out.println("##### Initialized! ####");
        System.out.println("#######################");
        System.out.println(" go to: http://localhost:8080");
    }

}
