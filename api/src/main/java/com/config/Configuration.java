package com.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Configuration {

    @Value("${steam.api.url.dota2}")
    private String dota2Url;

    @Value("${steam.api.version}")
    private String apiVersion;

    @Value("${steam.api.key}")
    private String apiKey;

    @Value("${steam.api.and}")
    private String apiAnd;

    @Value("${steam.api.language}")
    private String apiLanguage;

}
