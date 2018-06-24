package com.packtpub.yummy.ui;

import com.packtpub.yummy.model.Bookmark;
import com.packtpub.yummy.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('USER')")
@RequestMapping("/bookmark/")
public class BookmarkUiController {
    @Autowired
    BookmarkService bookmarkService;

    @ModelAttribute
    public Bookmark bookmark(@PathVariable Optional<UUID> id){
        return id
                .map(uuid -> bookmarkService.find(uuid))
                .orElseThrow(()-> new IncorrectResultSizeDataAccessException(1));
    }

    @GetMapping("{id}")
    public String details(@PathVariable UUID id, Model model) {
        return "bookmark/details";
    }
}
