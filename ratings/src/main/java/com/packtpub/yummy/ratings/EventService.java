package com.packtpub.yummy.ratings;

import com.packtpub.yummy.ratings.config.AmqpConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventService {
    private RatingRepository ratingRepository;

    @StreamListener(AmqpConfig.MessageSink.BOOKMARK_DELETIONS)
    public void processDelete(BookmarkDeleteEvent event) {
        ratingRepository.remove(event.getType(), event.getEntityId());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkDeleteEvent {
        String type, entityId;
    }
}
