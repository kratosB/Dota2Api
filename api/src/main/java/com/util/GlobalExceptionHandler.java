package com.util;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created on 2018/5/23.
 * 
 * @author zhiqiang bao 暂时没有启动
 */
// @ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public String processException(Exception e) {
        System.out.println(e.getLocalizedMessage());
        System.out.println(e.getStackTrace()[0]);
        System.out.println(e.getStackTrace()[1]);
        System.out.println(JsonMapper.nonDefaultMapper().toJson(e));
        System.out.println(e.getMessage());
        return e.getMessage();
    }

}
