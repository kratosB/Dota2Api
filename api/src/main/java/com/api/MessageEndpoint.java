package com.api;

import com.message.IMessageService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/6/6.
 *
 * @author zhiqiang bao
 */
@Slf4j
@RestController
public class MessageEndpoint {

    private IMessageService messageServiceImpl;

    public MessageEndpoint(IMessageService messageServiceImpl) {
        this.messageServiceImpl = messageServiceImpl;
    }

    @ApiOperation("发送短信")
    @GetMapping(value = "/api/message/send")
    public void sendMessage(@RequestParam long cellphone, @RequestParam String message) {
        log.info("开始发送短信，cellphone = {}，message = {}", cellphone, message);
        String result = messageServiceImpl.sendMessage(cellphone, message);
        log.info("结束发送短信，result = {}", result);
    }
}
