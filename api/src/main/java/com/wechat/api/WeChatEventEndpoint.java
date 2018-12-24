package com.wechat.api;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dao.MemoDao;
import com.dao.entity.Memo;
import com.util.JaxbUtil;
import com.wechat.api.req.MessageReq;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2018/12/24.
 *
 * @author zhiqiang bao
 */
@Slf4j
@RestController
public class WeChatEventEndpoint {

    private MemoDao memoDao;

    private RedisTemplate<String, String> redisTemplate;

    public WeChatEventEndpoint(MemoDao memoDao, RedisTemplate<String, String> redisTemplate) {
        this.memoDao = memoDao;
        this.redisTemplate = redisTemplate;
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
            String openId = messageReq.getFromUserName();
            String content = messageReq.getContent();
            String createdTime = messageReq.getCreateTime();
            log.info("收到文本消息，fromUser = {},content = {},createTime = {}", openId, content,createdTime);
            String redisKey = "Memo|" + openId;
            if (redisTemplate.hasKey(redisKey)) {
                if (StringUtils.equals(content, "1")) {
                    Memo memo = new Memo();
                    memo.setOpenId(openId);
                    memo.setContent(content);
                    memo.setCreatedTime(new Date());
                    memoDao.save(memo);
                } else if (StringUtils.equals(content, "2")) {
                    redisTemplate.delete(redisKey);
                } else {
                    String result = getResult(messageReq, "对不起，您的输入有误，保存备忘请输入1，不保存请输入2（或者不输入）");
                    log.info(result);
                    return result;
                }
            } else {
                ValueOperations<String, String> operations = redisTemplate.opsForValue();
                operations.set(redisKey, content, 60, TimeUnit.SECONDS);
                String result = getResult(messageReq,"收到内容为\""+content+"\"的消息，是否要存入备忘录，保存备忘请按1，不保存无需操作");
                return result;
            }
        } else if (StringUtils.equals(messageReq.getMsgType(), event)) {
            log.info("收到事件消息，fromUser = {},createTime = {},event = {},key = {}", messageReq.getFromUserName(),
                    messageReq.getCreateTime(), messageReq.getEvent(), messageReq.getEventKey());
            String result;
            switch (messageReq.getEventKey()) {
                case "WIFI":
                    break;
                case "WIFI_ACCOUNT":
                    result = getResult(messageReq, "WIFI账号是ChinaNet-TmVx");
                    log.info(result);
                    return result;
                case "WIFI_PASSWORD":
                    result = getResult(messageReq, "WIFI密码是uhhehvuc");
                    log.info(result);
                    return result;
                default:
                    result = getResult(messageReq, "你好");
                    log.info(result);
                    return result;
            }
            return null;
        } else {
            log.info("收到其他消息，fromUser = {},createTime = {},messageType = {}", messageReq.getFromUserName(),
                    messageReq.getCreateTime(), messageReq.getMsgType());
        }
        String result = getResult(messageReq, "你好");
        log.info(result);
        return result;
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

    private String getResult(MessageReq messageReq, String content) {
        return "<xml> <ToUserName>" + messageReq.getFromUserName() + "</ToUserName> <FromUserName>" + messageReq.getToUserName()
                + "</FromUserName> <CreateTime>" + messageReq.getCreateTime()
                + "</CreateTime> <MsgType><![CDATA[text]]></MsgType> <Content><![CDATA[" + content + "]]></Content> </xml>";
    }
}
