package com.util;

import com.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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

    private List<String> keyList;

    @Autowired
    public Gateway(RestTemplate restTemplate, Config config) {
        this.restTemplate = restTemplate;
        this.config = config;
        keyList = new ArrayList<>(3);
        config.setServiceAvailable(true);
    }

    public String getForObject(String url) {
        String response = getForObject(url, String.class);
        String serviceUnavailable503 = "503";
        String timeOut504 = "504";
        if (StringUtils.isBlank(response)) {
            throw new RuntimeException("由于503,504错误，暂停调用接口15分钟");
        } else if (response.contains(serviceUnavailable503) || response.contains(timeOut504)) {
            config.setServiceAvailable(false);
            throw new RuntimeException("发生503,504错误");
        } else {
            return response;
        }
    }

    private <T> T getForObject(String url, Class<T> clazz) {
        if (config.isServiceAvailable()) {
            url = url + getKey();
            return restTemplate.getForObject(url, clazz);
        } else {
            return null;
        }
    }

    private String getKey() {
        String key;
        if (keyList.isEmpty()) {
            key = config.getApiKey1();
            keyList.add(config.getApiKey2());
            keyList.add(config.getApiKey3());
        } else {
            key = keyList.get(0);
            keyList.remove(key);
        }
        return key;
    }

}
