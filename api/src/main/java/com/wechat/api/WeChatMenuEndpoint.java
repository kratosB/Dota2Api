package com.wechat.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.util.JsonMapper;
import com.wechat.service.IWeChatService;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2018/8/8.
 *
 * @author zhiqiang bao
 */
@Slf4j
@RestController
public class WeChatMenuEndpoint {

    private IWeChatService weChatService;

    public WeChatMenuEndpoint(IWeChatService weChatService) {
        this.weChatService = weChatService;
    }

    @GetMapping("api/weChat/createButton")
    public void createButton() {
        log.info("开始创建按钮");
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + weChatService.getAccessToken();

        String linkUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxbc1447205bc4595c&redirect_uri="
                + "http%3a%2f%2ftest1.iqunxing.com%2fauth%2fwechat%2fmenu%3furl%3dhttp%3a%2f%2fwww.baidu.com"
                + "&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect";

        Map<String, Object> subButtonMap1 = new HashMap<>(3);
        subButtonMap1.put("type", "view");
        subButtonMap1.put("name", "menu1");
        subButtonMap1.put("url", linkUrl);

        Map<String, Object> subButtonMap2 = new HashMap<>(3);
        subButtonMap2.put("type", "click");
        subButtonMap2.put("name", "获取WIFI账号");
        subButtonMap2.put("key", "WIFI_ACCOUNT");

        List<Object> subButtonList = new ArrayList<>(2);
        subButtonList.add(subButtonMap1);
        subButtonList.add(subButtonMap2);

        Map<String, Object> buttonMap1 = new HashMap<>(3);
        buttonMap1.put("type", "click");
        buttonMap1.put("name", "获取WIFI");
        buttonMap1.put("key", "WIFI");

        Map<String, Object> buttonMap2 = new HashMap<>(3);
        buttonMap2.put("type", "click");
        buttonMap2.put("name", "获取WIFI密码");
        buttonMap2.put("key", "WIFI_PASSWORD");

        Map<String, Object> buttonMap3 = new HashMap<>(2);
        buttonMap3.put("name", "菜单");
        buttonMap3.put("sub_button", subButtonList);

        List<Object> buttonList = new ArrayList<>(2);
        buttonList.add(buttonMap1);
        buttonList.add(buttonMap2);
        buttonList.add(buttonMap3);

        Map<String, Object> dataMap = new HashMap<>(1);
        dataMap.put("button", buttonList);

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

        log.info("结束创建按钮");
    }

    @GetMapping("api/weChat/deleteButton")
    public void deleteButton() {
        String url = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=" + weChatService.getAccessToken();
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            log.info(responseEntity.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
