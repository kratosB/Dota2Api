package com.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/5/22.
 * 
 * @author zhiqiang bao
 */
@RestController
public class HealthCheckEndpoint {

    @ApiOperation("health check")
    @RequestMapping(value = "api/health", method = {
            RequestMethod.POST, RequestMethod.GET
    })
    public Boolean healthCheck() {
        return true;
    }
}
