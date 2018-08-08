package com.wechat.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wechat.service.IWeChatService;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2018/8/8.
 *
 * @author zhiqiang bao
 */
@Slf4j
@Controller
public class WeChatRedirectEndpoint {

    private IWeChatService weChatService;

    @Autowired
    public WeChatRedirectEndpoint(IWeChatService weChatService) {
        this.weChatService = weChatService;
    }

    @GetMapping("api/redirectLocation")
    public void redirectLocation(@RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "userInfo", required = false) String userInfo) {
        log.info("redirectLocation，code = {}，userInfo = {}", code, userInfo);
        weChatService.getAuthToken(code);
    }
}
