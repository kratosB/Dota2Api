package com.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created on 2018/5/24.
 * 
 * @author zhiqiang bao
 */
@Service
public class Gateway {

    private RestTemplate restTemplate;

    @Autowired
    public Gateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getForObject(String url) {
        return restTemplate.getForObject(url, String.class);
    }

    public <T> T getForObject(String url, Class<T> clazz) {
        return restTemplate.getForObject(url, clazz);
    }

}
