package com.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.config.Config;
import com.event.HttpServerErrorExceptionEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2018/5/24.
 * 
 * @author zhiqiang bao
 */
@Slf4j
@Service
public class Gateway {

    private RestTemplate restTemplate;

    private Config config;

    private ApplicationEventPublisher applicationEventPublisher;

    private List<String> keyList;

    @Autowired
    public Gateway(RestTemplate restTemplate, Config config, ApplicationEventPublisher applicationEventPublisher) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.applicationEventPublisher = applicationEventPublisher;
        keyList = new ArrayList<>(3);
        config.setServiceAvailable(true);
    }

    public String getForString(String url) {
        return getForObject(url, String.class);
    }

    public Object getForObject(String url) {
        return getForObject(url, Object.class);
    }

    private <T> T getForObject(String url, Class<T> clazz) {
        if (config.isServiceAvailable()) {
            url = url + getKey();
            try {
                return restTemplate.getForObject(url, clazz);
            } catch (HttpServerErrorException httpServerErrorException) {
                String internalServerError500 = "500";
                String serviceUnavailable503 = "503";
                String timeOut504 = "504";
                if (httpServerErrorException.getMessage().contains(internalServerError500)) {
                    config.setServiceAvailable(false);
                    applicationEventPublisher.publishEvent(new HttpServerErrorExceptionEvent(new Object()));
                    throw new RuntimeException("发生500错误，url=" + url);
                } else if (httpServerErrorException.getMessage().contains(serviceUnavailable503)) {
                    config.setServiceAvailable(false);
                    applicationEventPublisher.publishEvent(new HttpServerErrorExceptionEvent(new Object()));
                    throw new RuntimeException("发生503错误，url=" + url);
                } else if (httpServerErrorException.getMessage().contains(timeOut504)) {
                    config.setServiceAvailable(false);
                    applicationEventPublisher.publishEvent(new HttpServerErrorExceptionEvent(new Object()));
                    throw new RuntimeException("发生504错误，url=" + url);
                } else {
                    log.error(httpServerErrorException.getLocalizedMessage());
                    throw httpServerErrorException;
                }
            }
        } else {
            throw new RuntimeException("由于503,504错误，暂停调用接口15分钟");
        }
    }

    private String getKey() {
        String key;
        if (keyList.isEmpty()) {
            key = config.getApiKey2();
            keyList.add(config.getApiKey3());
        } else {
            key = keyList.get(0);
            keyList.remove(key);
        }
        return key;
    }

}
