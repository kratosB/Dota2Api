package com.service.message;

/**
 * Created on 2018/6/6.
 *
 * @author zhiqiang bao
 */
public interface IMessageService {

    /**
     * 发送短信
     *
     * @param cellphone
     *            电话号码
     * @param message
     *            短信内容
     * @return 短信发送情况
     */
    String sendMessage(long cellphone, String message);
}
