package com.wechat.service;

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
     * 根据微信回调传来的code，获取authorization_code
     * 
     * @param code
     *            code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     * @return authorization_code
     */
    String getAuthToken(String code);

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
