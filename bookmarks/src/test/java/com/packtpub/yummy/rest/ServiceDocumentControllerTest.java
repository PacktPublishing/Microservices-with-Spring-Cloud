package com.packtpub.yummy.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Resource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", roles = "USER")
public class ServiceDocumentControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired  @Qualifier("_halObjectMapper")
    ObjectMapper mapper;

    @Test
    public void getServiceDocument() throws Exception {
        String result = mvc.perform(
                MockMvcRequestBuilders.get("/")
                .accept("application/hal+json;charset=UTF-8")
        ).andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith("application/hal+json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        Resource<String> value = mapper.readValue(result, new TypeReference<Resource<String>>() {
        });

        List<String> linkRels = value.getLinks().stream().map(link -> link.getRel()).collect(Collectors.toList());
        assertThat(linkRels, Matchers.hasItem("self"));
        assertEquals(value.getLink("self"), value.getId());

        assertTrue(value.hasLink("bookmarks"));
    }
}
