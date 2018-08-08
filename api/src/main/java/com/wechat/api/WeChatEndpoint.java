package com.wechat.api;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        String fromUserName = getValueFromMessage(message, "<FromUserName>", "</FromUserName>");
        log.info("fromUserName = {}", fromUserName);

        String toUserName = getValueFromMessage(message, "<ToUserName>", "</ToUserName>");
        log.info("toUserName = {}", toUserName);

        String createTime = getValueFromMessage(message, "<CreateTime>", "</CreateTime>");
        log.info("createTime = {}", createTime);

        String content = "Content";
        if (message.contains(content)) {
            content = getValueFromMessage(message, "<Content>", "</Content>");
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

    private String getValueFromMessage(String message, String pre, String post) {
        int preIndex = message.indexOf(pre) + pre.length();
        int postIndex = message.indexOf(post);
        return message.substring(preIndex, postIndex);
    }

}
