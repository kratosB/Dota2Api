package com.wechat.api;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.wechat.service.IWeChatService;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2018/7/18.
 *
 * @author zhiqiang bao
 */
@Slf4j
@RestController
public class WeChatEndpoint {

    private IWeChatService weChatService;

    @Autowired
    public WeChatEndpoint(IWeChatService weChatService) {
        this.weChatService = weChatService;
    }

    @GetMapping("api/weChat/getAccessToken")
    public String getAccessToken() {
        log.info("开始获取access_token");
        String accessToken = weChatService.getAccessToken();
        log.info("结束获取access_token，accessToken = {}", accessToken);
        return accessToken;
    }

    @GetMapping("api/weChat/createQRCode")
    public Map<String, String> createQRCode(@RequestParam(value = "sceneId") String sceneId) {
        log.info("开始生成临时二维码，sceneId = {}", sceneId);
        Map<String, String> resultMap = weChatService.createQRCode(sceneId);
        log.info("结束生成临时二维码，sceneId = {}", sceneId);
        return resultMap;
    }

    @GetMapping("api/weChat/getQRCode")
    public ResponseEntity<byte[]> getQRCode(@RequestParam(value = "sceneId") String sceneId) {
        Map<String, String> qrCode = createQRCode(sceneId);
        String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + qrCode.get("ticket");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        response.getHeaders().forEach((key, value) -> log.info(key + " = " + value));
        return response;
    }

}
