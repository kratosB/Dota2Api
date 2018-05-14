package com.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Configuration {

    @Value("${steam.api.url.dota2.IEcon}")
    private String iEconUrl;

    @Value("${steam.api.url.dota2.IDOTA2}")
    private String iDota2Url;

    @Value("${steam.api.url.steam.user}")
    private String steamUserUrl;

    @Value("${steam.api.version.v1}")
    private String apiVersion;

    @Value("${steam.api.key}")
    private String apiKey;

    @Value("${steam.api.and}")
    private String apiAnd;

    @Value("${steam.api.language.zh}")
    private String apiLanguage;

    @Value("${admin.steamId}")
    private String adminSteamId;

}
