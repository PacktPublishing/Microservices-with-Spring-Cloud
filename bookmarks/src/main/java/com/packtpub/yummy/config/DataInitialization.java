package com.packtpub.yummy.config;

import com.packtpub.yummy.model.Bookmark;
import com.packtpub.yummy.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class DataInitialization implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    BookmarkService bookmarkService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!bookmarkService.findAll().iterator().hasNext()) {
            bookmarkService.addBookmark(new Bookmark("Packt publishing", "http://packtpub.com"));
            bookmarkService.addBookmark(new Bookmark("orchit GmbH", "http://orchit.de"));
        }
    }
}