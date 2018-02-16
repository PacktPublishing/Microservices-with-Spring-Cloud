package com.packtpub.yummy.rest;

import com.packtpub.yummy.model.Bookmark;
import com.packtpub.yummy.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(value = "bookmarks", produces = {"application/hal+json;charset=UTF-8", MediaType.APPLICATION_JSON_UTF8_VALUE})
@PreAuthorize("hasRole('USER')")
public class BookmarksController {
    @Autowired
    BookmarkService bookmarkService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addBookmark(@RequestBody @Valid Bookmark bookmark){
        UUID uuid = bookmarkService.addBookmark(bookmark);
        return ResponseEntity.created(
                BasicLinkBuilder.linkToCurrentMapping()
                        .slash("bookmark")
                        .slash(uuid)
                        .toUri())
                .build();
    }

    @GetMapping
    public Resources<Bookmark> findAllBookmarks(){
        return new Resources<>(bookmarkService.findAll(),
                BasicLinkBuilder.linkToCurrentMapping()
                        .slash("bookmarks").withSelfRel()
                );
    }
}
