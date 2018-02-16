package com.packtpub.yummy.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packtpub.yummy.model.Bookmark;
import com.packtpub.yummy.model.MyUser;
import com.packtpub.yummy.service.BookmarkService;
import com.packtpub.yummy.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookmarksControllerWithUserDetailsTest {
    @Autowired
    MockMvc mvc;
    @SpyBean
    BookmarkService bookmarkService;
    @Autowired  @Qualifier("_halObjectMapper")
    ObjectMapper mapper;

    @Before
    public void setup() {
        Mockito.reset(bookmarkService);
    }

    @Test
    @WithUserDetails("admin")
    public void addABookmark() throws Exception {
        Bookmark value = new Bookmark("Packt publishing", "http://packtpub.com");
        addBookmark(value);
        Mockito.verify(bookmarkService, atLeastOnce()).addBookmark(Mockito.any(Bookmark.class));
    }

    private void addBookmark(Bookmark value) throws Exception {
        mvc.perform(
                post("/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(mapper.writeValueAsString(value))
                        .with(csrf())
        ).andExpect(status().isCreated());
    }

    @TestConfiguration
    public static class TestConfig{
        @Bean
        public UserService userService(){
            UserService service = mock(UserService.class);
            when(service.findUserByUsername("admin"))
                    .thenReturn(
                            MyUser.builder()
                            .username("admin")
                            .authorities(Arrays.asList(
                                    new SimpleGrantedAuthority("ROLE_USER"),
                                    new SimpleGrantedAuthority("ROLE_ADMIN")
                            )).build()
                    );
            return service;
        }
    }
}
