package com.util;

import com.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created on 2018/5/24.
 * 
 * @author zhiqiang bao
 */
@Slf4j
@Service
public class Gateway {

    private RestTemplate restTemplate;

    private List<String> keyList;

    @Autowired
    public Gateway(RestTemplate restTemplate, Config config) {
        this.restTemplate = restTemplate;
        keyList = new ArrayList<>(3);
        keyList.add(config.getApiKey1());
        keyList.add(config.getApiKey2());
        keyList.add(config.getApiKey3());
    }

    public String getForObject(String url) {
        String response = getForObject(url, String.class);
        String error503 = "503";
        String timeOut504 = "504";
        if (response.contains(error503) || response.contains(timeOut504)) {
            // TODO
            return "";
        } else {
            return response;
        }
    }

    public <T> T getForObject(String url, Class<T> clazz) {
        url = url + keyList.get(new Random().nextInt(3));
        return restTemplate.getForObject(url, clazz);
    }

}
