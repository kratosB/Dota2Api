package com.wechat.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.util.JsonMapper;
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

    public WeChatEndpoint(IWeChatService weChatService) {
        this.weChatService = weChatService;
    }

    @GetMapping("/weChat")
    public String getWeChat(@RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {
        log.info("获取到外部get请求, signature = {}, timestamp = {}, nonce = {}, echostr = {}", signature, timestamp, nonce, echostr);
        if (signature != null && validate(signature, timestamp, nonce)) {
            log.info("获取到外部get请求，验证通过");
            return echostr;
        } else {
            log.info("获取到外部get请求，验证不通过");
            return null;
        }
    }

    @PostMapping("/weChat")
    public String postWeChat(@RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce, @RequestBody(required = false) String message) {
        log.info("获取到外部post请求, signature = {}, timestamp = {}, nonce = {}, message = {}", signature, timestamp, nonce, message);
        if (signature != null && validate(signature, timestamp, nonce)) {
            log.info("获取到外部get请求，验证通过");
        } else {
            log.info("获取到外部get请求，验证不通过");
            return null;
        }
        int fromUserNameIndexPre = message.indexOf("<FromUserName>") + 14;
        int fromUserNameIndexPost = message.indexOf("</FromUserName>");
        String fromUserName = message.substring(fromUserNameIndexPre, fromUserNameIndexPost);
        log.info("fromUserName = {}", fromUserName);

        int toUserNameIndexPre = message.indexOf("<ToUserName>") + 12;
        int toUserNameIndexPost = message.indexOf("</ToUserName>");
        String toUserName = message.substring(toUserNameIndexPre, toUserNameIndexPost);
        log.info("toUserName = {}", toUserName);

        int createTimeIndexPre = message.indexOf("<CreateTime>") + 12;
        int createTimeIndexPost = message.indexOf("</CreateTime>");
        String createTime = message.substring(createTimeIndexPre, createTimeIndexPost);
        log.info("createTime = {}", createTime);

        String content = "Content";
        if (message.contains(content)) {
            int contentIndexPre = message.indexOf("<Content>") + 9;
            int contentIndexPost = message.indexOf("</Content>");
            content = message.substring(contentIndexPre, contentIndexPost);
            log.info("content = {}", content);
        }

        String result = "<xml> <ToUserName>" + fromUserName + "</ToUserName> <FromUserName>" + toUserName
                + "</FromUserName> <CreateTime>" + createTime
                + "</CreateTime> <MsgType><![CDATA[text]]></MsgType> <Content><![CDATA[你好]]></Content> </xml>";
        log.info(result);
        return result;
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

    @GetMapping("api/weChat/createButton")
    public void createButton() {
        log.info("开始创建按钮");
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + getAccessToken();

        String linkUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxbc1447205bc4595c&redirect_uri="
                + "http%3a%2f%2ftest1.iqunxing.com%2fauth%2fwechat%2fmenu%3furl%3dhttp%3a%2f%2fwww.baidu.com"
                + "&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect";

        Map<String, Object> subButtonMap1 = new HashMap<>(3);
        subButtonMap1.put("type", "view");
        subButtonMap1.put("name", "menu1");
        subButtonMap1.put("url", linkUrl);

        Map<String, Object> subButtonMap3 = new HashMap<>(3);
        subButtonMap3.put("type", "click");
        subButtonMap3.put("name", "testClick");
        subButtonMap3.put("key", "V1001_GOOD");

        List<Object> subButtonList = new ArrayList<>(2);
        subButtonList.add(subButtonMap1);
        subButtonList.add(subButtonMap3);

        Map<String, Object> buttonMap = new HashMap<>(3);
        buttonMap.put("type", "click");
        buttonMap.put("name", "点击绑定");
        buttonMap.put("key", "eventKey");

        Map<String, Object> buttonMap2 = new HashMap<>(2);
        buttonMap2.put("name", "菜单");
        buttonMap2.put("sub_button", subButtonList);

        List<Object> buttonList = new ArrayList<>(2);
        buttonList.add(buttonMap);
        buttonList.add(buttonMap2);

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
        String url = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=" + getAccessToken();
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            log.info(responseEntity.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @GetMapping("api/redirectLocation")
    public void redirectLocation(@RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "userInfo", required = false) String userInfo) {
        log.info("redirectLocation，code = {}，userInfo = {}", code, userInfo);
        weChatService.getAuthToken(code);
    }

    private boolean validate(String signature, String timestamp, String nonce) {
        log.info("开始验证请求是否是从微信来的，timestamp = {}，nonce = {}，signature = {}", timestamp, nonce, signature);
        String token = "bzq";
        String[] paramArray = new String[] {
                token, timestamp, nonce
        };
        Arrays.sort(paramArray);
        StringBuilder sb = new StringBuilder();
        for (String s : paramArray) {
            sb.append(s);
        }
        String generateCode = DigestUtils.sha1Hex(sb.toString());
        log.info("generateCode:" + generateCode);
        return generateCode.equals(signature);
    }

}
