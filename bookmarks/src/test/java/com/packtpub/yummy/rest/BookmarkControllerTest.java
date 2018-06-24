package com.packtpub.yummy.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.packtpub.yummy.model.Bookmark;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "admin", roles = {"ADMIN","USER"})
public class BookmarkControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired  @Qualifier("_halObjectMapper")
    ObjectMapper objectMapper;

    @Test
    public void getABookmark() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        Resource<Bookmark> output = getBookmark(location);
        assertNotNull(output.getContent().getUrl());
        assertEquals(input.getUrl(), output.getContent().getUrl());
    }

    @Test
    public void deleteABookmark() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        mvc.perform(
                delete(location).accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                .with(csrf())
        ).andDo(print()).andExpect(status().isOk());

        mvc.perform(
                get(location).accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
        ).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void deleteABookmarkTwiceYieldsNotModified() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        mvc.perform(
                delete(location).accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .with(csrf())
        ).andDo(print()).andExpect(status().isOk());

        mvc.perform(
                delete(location).accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .with(csrf())
        ).andDo(print()).andExpect(status().isNotModified());
    }

    @Test
    public void updateABookmark() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        Resource<Bookmark> output = getBookmark(location);

        String result = mvc.perform(
                put(output.getId().getHref())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(output.getContent().withUrl("http://kulinariweb.de")))
                        .with(csrf())
        ).andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        output = objectMapper.readValue(result, new TypeReference<Resource<Bookmark>>() {
        });

        assertEquals("http://kulinariweb.de", output.getContent().getUrl());
    }

    @Test
    public void updateABookmarkFailsBecauseDescriptionIsNull() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        Resource<Bookmark> output = getBookmark(location);
        output.getContent().setDescription(null);
        mvc.perform(
                put(output.getId().getHref())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(output.getContent()))
                        .with(csrf())
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("description"));
    }

    @Test
    public void updateABookmarkFailsBecauseUrlIsNotValid() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        Resource<Bookmark> output = getBookmark(location);
        output.getContent().setUrl("broken://url.me");
        mvc.perform(
                put(output.getId().getHref())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(output.getContent()))
                        .with(csrf())
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("url"));
    }

    @Test
    public void updateABookmarkStaleFails() throws Exception {
        Bookmark input = getSimpleBookmark();
        String location = addBookmark(input);

        Resource<Bookmark> output = getBookmark(location);
        mvc.perform(
                put(output.getId().getHref())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(output.getContent().withUrl("http://kulinariweb.de")))
                        .with(csrf())
        ).andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mvc.perform(
                put(output.getId().getHref())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(output.getContent().withUrl("http://kulinariweb2.de")))
                        .with(csrf())
        ).andDo(print()).andExpect(status().isConflict());
    }

    @Test
    public void updateABookmarkFailWrongId() throws Exception {
        Bookmark input = getSimpleBookmark();

        mvc.perform(
                put("/bookmark/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(input))
                        .with(csrf())
        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Bookmark getSimpleBookmark() {
        return new Bookmark("Packt publishing", "http://packtpub.com");
    }

    private Resource<Bookmark> getBookmark(String location) throws Exception {
        String result = mvc.perform(
                get(location)
                        .accept("application/hal+json;charset=UTF-8", "application/json;charset=UTF-8")
        ).andDo(print())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(result, new TypeReference<Resource<Bookmark>>() {
        });
    }

    private String addBookmark(Bookmark input) throws Exception {
        return mvc.perform(
                post("/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(input))
                        .with(csrf())
        ).andDo(print()).andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }
}
