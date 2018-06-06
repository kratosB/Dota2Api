package com.message;

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
     */
    void sendMessage(long cellphone,String message);
}
