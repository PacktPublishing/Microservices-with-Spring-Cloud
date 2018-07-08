package com.packtpub.yummy.tracing;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;

@Component
public class CorrelationIdHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        Map<String, String> map = MDC.getCopyOfContextMap();
        return () -> {
            try {
                MDC.setContextMap(map);
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }
}
