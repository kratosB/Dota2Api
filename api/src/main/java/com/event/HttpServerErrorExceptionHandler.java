package com.event;

import com.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created on 2018/5/26.
 *
 * @author zhiqiang bao
 */
@Slf4j
@Service
public class HttpServerErrorExceptionHandler {

    private Config config;

    public HttpServerErrorExceptionHandler(Config config) {
        this.config = config;
    }

    @EventListener
    public void enableGateway(final HttpServerErrorExceptionEvent httpServerErrorExceptionEvent) {
        log.warn(new Date() + "，事件被触发，此时ServiceAvailable={}", config.isServiceAvailable());
        try {
            Thread.sleep(900000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        config.setServiceAvailable(true);
        log.warn(new Date() + "，事件处理结束，此时ServiceAvailable={}", config.isServiceAvailable());
    }
}
