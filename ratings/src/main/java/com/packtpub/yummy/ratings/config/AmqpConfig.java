package com.packtpub.yummy.ratings.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.SubscribableChannel;

@Configuration
@EnableBinding(AmqpConfig.MessageSink.class)
public class AmqpConfig {
    public interface MessageSink {
        String BOOKMARK_DELETIONS = "bookmarkDeletions";

        @Input
        SubscribableChannel bookmarkDeletions();
    }
}
