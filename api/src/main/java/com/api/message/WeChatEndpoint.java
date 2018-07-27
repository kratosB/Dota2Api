package com.api.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import lombok.Data;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JavaType;
import com.util.JsonMapper;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * Created on 2018/7/18.
 *
 * @author zhiqiang bao
 */
@Slf4j
@RestController
public class WeChatEndpoint {

    @GetMapping("/weChat")
    public String getWeChat(@RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {
        log.info("获取到外部get请求, signature = {}, timestamp = {}, nonce = {}, echostr = {}", signature, timestamp, nonce, echostr);
        return echostr;
    }

    @PostMapping("/weChat")
    public String postWeChat(@RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce, @RequestBody(required = false) String message) {
        log.info("获取到外部get请求, signature = {}, timestamp = {}, nonce = {}, message = {}", signature, timestamp, nonce, message);

        int fromUserNameIndexPre = message.indexOf("<FromUserName>") + 14;
        int fromUserNameIndexPost = message.indexOf("</FromUserName>");
        String fromUserName = message.substring(fromUserNameIndexPre, fromUserNameIndexPost);

        int toUserNameIndexPre = message.indexOf("<ToUserName>") + 12;
        int toUserNameIndexPost = message.indexOf("</ToUserName>");
        String toUserName = message.substring(toUserNameIndexPre, toUserNameIndexPost);

        int createTimeIndexPre = message.indexOf("<CreateTime>") + 12;
        int createTimeIndexPost = message.indexOf("</CreateTime>");
        String createTime = message.substring(createTimeIndexPre, createTimeIndexPost);

        String result = "<xml> <ToUserName>" + fromUserName + "</ToUserName> <FromUserName>" + toUserName
                + "</FromUserName> <CreateTime>" + createTime
                + "</CreateTime> <MsgType>< ![CDATA[text] ]></MsgType> <Content>< ![CDATA[你好] ]></Content> </xml>";

        XStream xstream = new XStream(new DomDriver());
        ReplyTextMessage we = new ReplyTextMessage();
        we.setMessageType("text");
        we.setCreateTime(createTime);
        we.setContent("你好");
        we.setToUserName(fromUserName);
        we.setFromUserName(toUserName);
        xstream.alias("xml", ReplyTextMessage.class);
        xstream.aliasField("ToUserName", ReplyTextMessage.class, "toUserName");
        xstream.aliasField("FromUserName", ReplyTextMessage.class, "fromUserName");
        xstream.aliasField("CreateTime", ReplyTextMessage.class, "createTime");
        xstream.aliasField("MsgType", ReplyTextMessage.class, "messageType");
        xstream.aliasField("Content", ReplyTextMessage.class, "content");
        xstream.aliasField("FuncFlag", ReplyTextMessage.class, "funcFlag");
        String xml = xstream.toXML(we);

        log.info(xml);
        return xml;
    }

    @GetMapping("/weChat/getAccessToken")
    public void getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        String appId = "wxadd9411e3a7a34ed";
        String appKey = "c70637c774cea4a168e83e67388061ac";
        String grantType = "client_credential";
        String url = "https://api.weixin.qq.com/cgi-bin/token?" + "appid=" + appId + "&" + "secret=" + appKey + "&" + "grant_type="
                + grantType;
        String result = restTemplate.getForObject(url, String.class);
        JavaType mapType = JsonMapper.nonDefaultMapper().contructMapType(HashMap.class, String.class, String.class);
        Map<String, String> resultMap = JsonMapper.nonDefaultMapper().fromJson(result, mapType);
        for (String key : resultMap.keySet()) {
            log.info("key = " + key + ", value = " + resultMap.get(key));
        }
    }

}

@Data
class ReplyTextMessage {

    private String toUserName;

    private String fromUserName;

    private String createTime;

    private String messageType;

    private String content;

    private String funcFlag;
}
