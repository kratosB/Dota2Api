package com.service.message;

import java.util.Map;

/**
 * Created on 2018/7/31.
 *
 * @author zhiqiang bao
 */
public interface IWeChatService {

    /**
     * 获取access_token
     * 
     * @return access_token
     */
    String getAccessToken();

    /**
     * 发送消息给关注的人
     * 
     * @param toUser
     *            接收人的openId
     * @param message
     *            消息内容
     */
    void sendMessage(String toUser, String message);

    void sendTemplateMessage(String toUser, String templateCode, String customerName);

    Map<String, String> createQRCode(String sceneId);
}
