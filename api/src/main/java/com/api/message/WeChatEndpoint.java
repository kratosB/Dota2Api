package com.api.message;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.service.message.IWeChatService;

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

        if (message.contains("Content")) {
            int contentIndexPre = message.indexOf("<Content>") + 9;
            int contentIndexPost = message.indexOf("</Content>");
            String content = message.substring(contentIndexPre, contentIndexPost);
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

    @GetMapping("api/weChat/sendMsg")
    public void sendMsg(@RequestParam(value = "toUser") String toUser,@RequestParam(value = "message") String message) {
        log.info("开始发送微信消息，toUser = {}，message = {}",toUser, message);
        toUser = "oYSUd1Nx3Ucq-1CHCDrMijbtX4x8";
        weChatService.sendMessage(toUser,message);
        log.info("结束发送微信消息");
    }

    @GetMapping("api/api/weChat/sendTemplateMsg")
    public void sendTemplateMsg(@RequestParam(value = "toUser") String toUser,
            @RequestParam(value = "templateCode") String templateCode, @RequestParam(value = "customerName") String customerName) {
        log.info("开始发送微信模板消息，toUser = {}，templateCode = {}，customerName = {}", toUser, templateCode, customerName);
        weChatService.sendTemplateMessage(toUser, templateCode, customerName);
        log.info("结束发送微信模板消息");
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
