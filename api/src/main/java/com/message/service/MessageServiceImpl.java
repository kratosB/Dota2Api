package com.message.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.config.Config;
import com.util.JsonMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2018/6/6.
 *
 * @author zhiqiang bao
 */
@Slf4j
@Service
public class MessageServiceImpl implements IMessageService {

    private Config config;

    @Autowired
    public MessageServiceImpl(Config config) {
        this.config = config;
    }

    private String url = "https://yun.tim.qq.com/v5/tlssmssvr/sendsms?";

    @Override
    public String sendMessage(long cellphone, String message) {
        String randomString = UUID.randomUUID().toString();
        url = url + "sdkappid=" + config.getAppId() + "&random=" + randomString;
        Map<String, Object> data = new HashMap<>(10);
        Map<String, Object> tel = new HashMap<>(3);
        tel.put("mobile", String.valueOf(cellphone));
        tel.put("nationcode", "86");
        data.put("msg", message);
        long time = System.currentTimeMillis() / 1000;
        String sig = DigestUtils
                .sha256Hex("appkey=" + config.getAppKey() + "&random=" + randomString + "&time=" + time + "&mobile=" + cellphone);
        data.put("sig", sig);
        data.put("tel", tel);
        data.put("time", time);
        data.put("type", 0);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<>(JsonMapper.nonDefaultMapper().toJson(data), headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> result = restTemplate.postForEntity(url, formEntity, String.class);
            return result.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
