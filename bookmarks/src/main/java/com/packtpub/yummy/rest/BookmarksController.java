package com.packtpub.yummy.rest;

import com.packtpub.yummy.model.Bookmark;
import com.packtpub.yummy.model.BookmarkResourceAssembler;
import com.packtpub.yummy.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "bookmarks", produces = {"application/hal+json;charset=UTF-8", MediaType.APPLICATION_JSON_UTF8_VALUE})
@PreAuthorize("hasRole('USER')")
public class BookmarksController {
    @Autowired
    BookmarkResourceAssembler resourceAssembler;
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
    public Resources<Resource<Bookmark>> findAllBookmarks(HttpServletRequest request){
        Resources<Resource<Bookmark>> bookmarks = new Resources<>(
                resourceAssembler.toResourceList(bookmarkService.findAll()),
                BasicLinkBuilder.linkToCurrentMapping()
                        .slash("bookmarks").withSelfRel()
        );
        if(request.isUserInRole("ADMIN")){
            bookmarks.add(
                    linkTo(methodOn(BookmarksController.class).addBookmark(null)).withRel("add"),
                    linkTo(methodOn(BookmarkController.class).getBookmarkTemplate()).withRel("template")
            );
        }
        return bookmarks;
    }
}
