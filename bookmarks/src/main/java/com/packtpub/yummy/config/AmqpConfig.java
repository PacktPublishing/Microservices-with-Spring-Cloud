package com.packtpub.yummy.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableBinding(AmqpConfig.EventSource.class)
public class AmqpConfig {
    public interface EventSource {
        String BOOKMARK_DELETIONS = "bookmarkDeletions";

        @Output(BOOKMARK_DELETIONS)
        MessageChannel bookmarkDeletions();
    }
}
