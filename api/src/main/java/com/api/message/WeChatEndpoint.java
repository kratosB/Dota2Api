package com.api.message;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2018/7/18.
 *
 * @author zhiqiang bao
 */
@Slf4j
@RestController
public class WeChatEndpoint {

    @GetMapping("/weChat")
    public void weChat(HttpServletRequest request, HttpServletResponse response) {
        log.info("获取到外部请求");
    }

}
