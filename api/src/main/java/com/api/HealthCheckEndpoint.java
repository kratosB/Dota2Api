package com.api;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2018/5/22.
 * 
 * @author zhiqiang bao
 */
@Slf4j
@RestController
public class HealthCheckEndpoint {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @ApiOperation("health check")
    @RequestMapping(value = "api/health", method = {
            RequestMethod.POST, RequestMethod.GET
    })
    public Boolean healthCheck() {
        return true;
    }

    @GetMapping(value = "api/redisTest")
    public String redisTest(@RequestParam String key) {
        if (redisTemplate.hasKey(key)) {
            Long expire = redisTemplate.getExpire(key);
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            String value = operations.get(key);
            log.info("key = {}, value = {}, expire = {}", key, value, expire);
            return value;
        } else {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            String value = "value";
            operations.set(key, value, 1200, TimeUnit.SECONDS);
            return "1";
        }
    }
}
