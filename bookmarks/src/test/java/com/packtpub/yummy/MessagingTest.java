package com.packtpub.yummy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.packtpub.yummy.config.AmqpConfig;
import com.packtpub.yummy.service.BookmarkService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessagingTest {
    @Autowired
    BookmarkService service;
    @MockBean
    JdbcTemplate jdbcTemplate;
    @Autowired
    AmqpConfig.EventSource source;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MessageCollector collector;

    @Test
    public void messageIsSent() throws JsonProcessingException {
        UUID id = UUID.randomUUID();
        when(jdbcTemplate.update(anyString(), eq(id.toString()))).thenReturn(1);

        service.delete(id);

        Message<BookmarkService.DeletionEvent> received =
                (Message<BookmarkService.DeletionEvent>) collector.forChannel(source.bookmarkDeletions()).poll();
        assertThat(received.getPayload()).isEqualTo(
                mapper.writer().writeValueAsString(
                        new BookmarkService.DeletionEvent("bookmark", id.toString())
                )
        );
    }

}
