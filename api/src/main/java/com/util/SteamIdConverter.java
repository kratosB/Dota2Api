package com.util;

/**
 * Created on 2018/4/19.
 *
 * @author zhiqiang bao
 */
public class SteamIdConverter {

    public String convert(String inputId) {
        Long constant = 76561197960265728L;
        Long id = Long.parseLong(inputId);
        if (constant > id) {
            Long steamId64 = constant + id;
            return String.valueOf(steamId64);
        } else {
            Long steamId32 = id - constant;
            return String.valueOf(steamId32);
        }
    }

    public static SteamIdConverter defaultInstance() {
        return new SteamIdConverter();
    }

}
