package com.wechat.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JavaType;
import com.util.JsonMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2018/7/31.
 *
 * @author zhiqiang bao
 */
@Slf4j
@Service
public class WeChatServiceImpl implements IWeChatService {

    private final String weChatApiAddress = "https://api.weixin.qq.com";

    private String appId = "1";

    private String appKey = "2";

    @Autowired
    public WeChatServiceImpl(RedisTemplate<String, String> redisTemplate) {
        ValueOperations<String, String> stringObjectValueOperations = redisTemplate.opsForValue();
        appId = stringObjectValueOperations.get("appId");
        appKey = stringObjectValueOperations.get("appKey");
        log.info("appId = {}, appKey = {}", appId, appKey);
    }

    @Override
    public String getAccessToken() {
        String grantType = "&grant_type=client_credential";
        String url = weChatApiAddress + "/cgi-bin/token?appid=" + appId + "&secret=" + appKey + grantType;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        JavaType mapType = JsonMapper.nonEmptyMapper().contructMapType(HashMap.class, String.class, String.class);
        Map<String, String> resultMap = JsonMapper.nonEmptyMapper().fromJson(result, mapType);
        String accessToken = resultMap.get("access_token");
        log.info("accessToken = {}", accessToken);
        return accessToken;
    }

    @Override
    public String getAuthToken(String code) {
        String grantType = "&grant_type=authorization_code";
        String url = weChatApiAddress + "/sns/oauth2/access_token?appid=" + appId + "&secret=" + appKey + "&code=" + code
                + grantType;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        log.info("authorization_code = {}", result);
        JavaType mapType = JsonMapper.nonEmptyMapper().contructMapType(HashMap.class, String.class, String.class);
        Map<String, String> resultMap = JsonMapper.nonEmptyMapper().fromJson(result, mapType);
        String accessToken = resultMap.get("access_token");
        log.info("accessToken = {}", accessToken);
        return accessToken;
    }

    @Override
    public void sendMessage(String toUser, String message) {
        String url = weChatApiAddress + "/cgi-bin/message/custom/send?access_token=" + getAccessToken();
        Map<String, String> textMap = new HashMap<>(2);
        textMap.put("content", message);
        Map<String, Object> dataMap = new HashMap<>(2);
        dataMap.put("text", textMap);
        dataMap.put("touser", toUser);
        dataMap.put("msgtype", "text");
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<>(JsonMapper.nonEmptyMapper().toJson(dataMap), headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, formEntity, String.class);
            log.info(responseEntity.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void sendTemplateMessage(String toUser, String templateCode, String customerName) {
        String url = weChatApiAddress + "/cgi-bin/message/template/send?access_token=" + getAccessToken();
        Map<String, String> customerNameMap = new HashMap<>(2);
        customerNameMap.put("value", customerName);
        customerNameMap.put("color", "#173177");
        Map<String, Object> data = new HashMap<>(1);
        data.put("customerName", customerNameMap);
        Map<String, Object> dataMap = new HashMap<>(3);
        dataMap.put("touser", toUser);
        dataMap.put("template_id", templateCode);
        dataMap.put("data", data);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<>(JsonMapper.nonEmptyMapper().toJson(dataMap), headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, formEntity, String.class);
            log.info(responseEntity.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public Map<String, String> createQRCode(String sceneId) {
        String url = weChatApiAddress + "/cgi-bin/qrcode/create?access_token=" + getAccessToken();
        Map<String, String> sceneMap = new HashMap<>(2);
        sceneMap.put("scene_str", sceneId);
        Map<String, Object> actionInfoMap = new HashMap<>(2);
        actionInfoMap.put("scene", sceneMap);
        Map<String, Object> dataMap = new HashMap<>(2);
        dataMap.put("action_info", actionInfoMap);
        dataMap.put("expire_seconds", 1200);
        dataMap.put("action_name", "QR_STR_SCENE");
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<>(JsonMapper.nonEmptyMapper().toJson(dataMap), headers);
        String result;
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, formEntity, String.class);
            result = responseEntity.getBody();
            log.info(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        JavaType mapType = JsonMapper.nonEmptyMapper().contructMapType(HashMap.class, String.class, String.class);
        return JsonMapper.nonEmptyMapper().fromJson(result, mapType);
    }
}
