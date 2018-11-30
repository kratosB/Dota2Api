package com.wechat.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import com.dao.MemoDao;
import com.dao.entity.Memo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.util.JaxbUtil;
import com.wechat.api.req.MessageReq;
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

    private MemoDao memoDao;

    @Autowired
    public WeChatEndpoint(IWeChatService weChatService,MemoDao memoDao) {
        this.weChatService = weChatService;
        this.memoDao = memoDao;
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
            log.info("获取到外部post请求，验证通过");
        } else {
            log.info("获取到外部post请求，验证不通过");
            return null;
        }

        JaxbUtil jaxbUtil = new JaxbUtil(MessageReq.class);
        MessageReq messageReq = jaxbUtil.fromXml(message);
        String event = "event";
        String text = "text";
        if (StringUtils.equals(messageReq.getMsgType(), text)) {
            log.info("收到文本消息，fromUser = {},createTime = {},content = {}", messageReq.getFromUserName(), messageReq.getCreateTime(),
                    messageReq.getContent());
            String openId = messageReq.getFromUserName();
            String content = messageReq.getContent();
            Memo memo = new Memo();
            memo.setOpenId(openId);
            memo.setContent(content);
            memo.setCreatedTime(new Date());
            memoDao.save(memo);
        } else if (StringUtils.equals(messageReq.getMsgType(), event)) {
            log.info("收到事件消息，fromUser = {},createTime = {},event = {},key = {}", messageReq.getFromUserName(),
                    messageReq.getCreateTime(), messageReq.getEvent(), messageReq.getEventKey());
            String result;
            switch (messageReq.getEventKey()) {
                case "WIFI":
                    break;
                case "WIFI_ACCOUNT":
                    result = "<xml> <ToUserName>" + messageReq.getFromUserName() + "</ToUserName> <FromUserName>"
                            + messageReq.getToUserName() + "</FromUserName> <CreateTime>" + messageReq.getCreateTime()
                            + "</CreateTime> <MsgType><![CDATA[text]]></MsgType> <Content><![CDATA[WIFI账号是ChinaNet-TmVx]]></Content> </xml>";
                    log.info(result);
                    return result;
                case "WIFI_PASSWORD":
                    result = "<xml> <ToUserName>" + messageReq.getFromUserName() + "</ToUserName> <FromUserName>"
                            + messageReq.getToUserName() + "</FromUserName> <CreateTime>" + messageReq.getCreateTime()
                            + "</CreateTime> <MsgType><![CDATA[text]]></MsgType> <Content><![CDATA[WIFI密码是uhhehvuc]]></Content> </xml>";
                    log.info(result);
                    return result;
                default:
                    result = "<xml> <ToUserName>" + messageReq.getFromUserName() + "</ToUserName> <FromUserName>"
                            + messageReq.getToUserName() + "</FromUserName> <CreateTime>" + messageReq.getCreateTime()
                            + "</CreateTime> <MsgType><![CDATA[text]]></MsgType> <Content><![CDATA[你好]]></Content> </xml>";
                    log.info(result);
                    return result;
            }
            return null;
        } else {
            log.info("收到其他消息，fromUser = {},createTime = {},messageType = {}", messageReq.getFromUserName(),
                    messageReq.getCreateTime(), messageReq.getMsgType());
        }
        String result = "<xml> <ToUserName>" + messageReq.getFromUserName() + "</ToUserName> <FromUserName>"
                + messageReq.getToUserName() + "</FromUserName> <CreateTime>" + messageReq.getCreateTime()
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
