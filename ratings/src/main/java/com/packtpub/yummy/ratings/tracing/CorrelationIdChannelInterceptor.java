package com.packtpub.yummy.ratings.tracing;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.integration.IntegrationMessageHeaderAccessor.CORRELATION_ID;

@Component
@Slf4j
@GlobalChannelInterceptor(order = Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdChannelInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        if (message.getHeaders().containsKey(CORRELATION_ID)) {
            MDC.put(CORRELATION_ID, (String) message.getHeaders().get(CORRELATION_ID));
        } else {
            message = MessageBuilder
                    .withPayload(message.getPayload())
                    .copyHeaders(message.getHeaders())
                    .setHeader(CORRELATION_ID, MDC.get(CORRELATION_ID))
                    .build();
        }
        log.info("PreSend Message {}", message.getHeaders().get(CORRELATION_ID));
        return message;
    }
}
