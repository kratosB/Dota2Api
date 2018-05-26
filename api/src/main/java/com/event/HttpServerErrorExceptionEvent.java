package com.event;

import org.springframework.context.ApplicationEvent;

/**
 * Created on 2018/5/26.
 *
 * @author zhiqiang bao
 */
public class HttpServerErrorExceptionEvent extends ApplicationEvent {

    public HttpServerErrorExceptionEvent(Object source) {
        super(source);
    }
}
