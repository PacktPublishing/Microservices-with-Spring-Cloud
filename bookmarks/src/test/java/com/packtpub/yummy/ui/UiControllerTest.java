package com.packtpub.yummy.ui;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.packtpub.yummy.model.Bookmark;
import com.packtpub.yummy.rest.BookmarksController;
import com.packtpub.yummy.service.BookmarkService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
public class UiControllerTest {
    List<Bookmark> bookmarks = Arrays.asList(
            new Bookmark("Packt publishing",
                    "http://packtpub.com").withUuid(UUID.randomUUID()),
            new Bookmark("orchit GmbH homepage", "http://orchit.de")
                    .withUuid(UUID.randomUUID())
    );

    @Autowired
    WebClient wc;

    @Autowired
    MockMvc mvc;

    @MockBean
    BookmarkService bookmarkService;

    @Before
    public void before() {
        when(bookmarkService.findAll()).thenReturn(bookmarks);
    }

    @Test
    public void testFindAllViaHtmlUnit() throws Exception {
// You can for example create a test that loads a page and checks for errors or errors and warnings. Of
// course it would make sense to collect all errors and fail later if there is more than one,
// so you can fix all errors at once and the run the test again. Currently our page contains html errors, so I disabled it.
//        wc.setHTMLParserListener(new HTMLParserListener() {
//            @Override
//            public void error(String message, URL url, String html, int line, int column, String key) {
//                fail("Error: "+message+" line: "+line+" column :"+column);
//            }
//
//            @Override
//            public void warning(String message, URL url, String html, int line, int column, String key) {
//                fail("Warning: "+message+" line: "+line+" column :"+column);
//            }
//        });

        String s = wc.getPage("/").getWebResponse().getContentAsString();
        assertThat(s, containsString("orchit.de"));

        Page page= wc.getPage("/");
        assertTrue(page.isHtmlPage());
        HtmlPage html= (HtmlPage)page;

        HtmlTable table = html.getBody().getFirstByXPath("//table");
        assertNotNull(table);
        assertEquals(bookmarks.size(), table.getRowCount());

        HtmlButton btn = html.getBody().getFirstByXPath("//table//form/button");
        assertNotNull(btn);
        btn.click();

        HtmlTable table2 =html.getBody().querySelector("table");
        assertNotNull(table2);
        assertEquals(bookmarks.size(), table2.getRowCount());

    }

    @Test
    public void testFindAllViaMockMvc() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/bookmarks").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.handler().methodCall(
                        MvcUriComponentsBuilder.on(BookmarksController.class).findAllBookmarks()
                ));
    }

}
