package com.packtpub.yummy.ratings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.packtpub.yummy.ratings.config.AmqpConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessagingTests {
    @MockBean
    RatingRepository ratingRepository;
    @Autowired
    AmqpConfig.MessageSink sink;
    @Autowired
    ObjectMapper mapper;

    @Test
    public void messageIsProcessed() throws JsonProcessingException {
        String payload = mapper.writer().writeValueAsString(
                new EventService.BookmarkDeleteEvent("1", "2")
        );

        sink.bookmarkDeletions().send(MessageBuilder.withPayload(payload).build());

        verify(ratingRepository).remove("1", "2");
    }

}
