package com.packtpub.yummy.model;

import com.packtpub.yummy.model.Bookmark;
import com.packtpub.yummy.rest.BookmarkController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class BookmarkResourceAssembler implements ResourceAssembler<Bookmark, Resource<Bookmark>> {

    @Override
    public Resource<Bookmark> toResource(Bookmark entity) {
        return new Resource<>(entity,
                linkTo(methodOn(BookmarkController.class).getBookmark(entity.getUuid())).withSelfRel());
    }
}
