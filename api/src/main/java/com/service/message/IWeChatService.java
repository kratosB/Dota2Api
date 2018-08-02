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

    /**
     * 发送模板消息给关注的人
     * 
     * @param toUser
     *            接收人的openId
     * @param templateCode
     *            模板id
     * @param customerName
     *            模板中的参数
     */
    void sendTemplateMessage(String toUser, String templateCode, String customerName);

    /**
     * 生成包含参数的临时二维码
     * 
     * @param sceneId
     *            参数
     * @return 二维码信息
     */
    Map<String, String> createQRCode(String sceneId);
}
