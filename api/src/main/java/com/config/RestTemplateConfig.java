package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Created on 2018/5/24.
 * 
 * @author zhiqiang bao
 */
@Configuration
public class RestTemplateConfig {

    @Autowired
    public RestTemplateConfig(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    private RestTemplateBuilder restTemplateBuilder;

    @Bean
    public RestTemplate restTemplate() {
        return restTemplateBuilder.build();
    }
}
