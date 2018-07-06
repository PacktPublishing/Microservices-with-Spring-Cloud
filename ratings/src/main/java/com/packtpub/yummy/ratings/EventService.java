package com.packtpub.yummy.ratings;

import com.packtpub.yummy.ratings.config.AmqpConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {
    private RatingRepository ratingRepository;

    @StreamListener(AmqpConfig.MessageSink.BOOKMARK_DELETIONS)
    public void processDelete(BookmarkDeleteEvent event) {
        log.info("Received Delete event for {}", event);
        ratingRepository.remove(event.getType(), event.getEntityId());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkDeleteEvent {
        String type, entityId;
    }
}
