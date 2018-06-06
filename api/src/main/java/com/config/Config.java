package com.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhiqiang bao
 */
@Getter
@Component
public class Config {

    @Value("${steam.api.url.dota2.IEcon}")
    private String iEconUrl;

    @Value("${steam.api.url.dota2.IDOTA2}")
    private String iDota2Url;

    @Value("${steam.api.url.steam.user}")
    private String steamUserUrl;

    @Value("${steam.api.version.v1}")
    private String apiVersion;

    @Value("${steam.api.key.first}")
    private String apiKey1;

    @Value("${steam.api.key.second}")
    private String apiKey2;

    @Value("${steam.api.key.third}")
    private String apiKey3;

    @Value("${steam.api.and}")
    private String apiAnd;

    @Value("${steam.api.language.zh}")
    private String apiLanguage;

    @Value("${admin.steamId}")
    private String adminSteamId;

    @Value("${message_appId}")
    private String appId;

    @Value("${message_appKey}")
    private String appKey;

    private boolean serviceAvailable;

    public void setServiceAvailable(boolean serviceAvailable) {
        this.serviceAvailable = serviceAvailable;
    }

}
