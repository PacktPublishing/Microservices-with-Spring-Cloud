package com.packtpub.yummy.model;

import com.packtpub.yummy.rest.BookmarkController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class BookmarkResourceAssembler implements ResourceAssembler<Bookmark, Resource<Bookmark>> {

    @Autowired
    HttpServletRequest request;

    @Override
    public Resource<Bookmark> toResource(Bookmark entity) {
        Resource<Bookmark> resource = new Resource<>(entity,
                linkTo(methodOn(BookmarkController.class).getBookmark(entity.getUuid())).withSelfRel());
        if(request.isUserInRole("ADMIN")){
            resource.add(
                    linkTo(methodOn(BookmarkController.class).getBookmark(entity.getUuid())).withRel("update"),
                    linkTo(methodOn(BookmarkController.class).getBookmark(entity.getUuid())).withRel("delete")
            );
        }
        return resource;
    }

    public List<Resource<Bookmark>> toResourceList(Iterable<Bookmark> list){
        return StreamSupport.stream(list.spliterator(),false)
                .map(this::toResource)
                .collect(Collectors.toList());
    }
}
