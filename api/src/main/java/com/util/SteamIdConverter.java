package com.util;

/**
 * Created on 2018/4/19.
 *
 * @author zhiqiang bao
 */
public class SteamIdConverter {

    private Long constant = 76561197960265728L;

    public String getId64(String inputId) {
        Long id = Long.parseLong(inputId);
        if (constant > id) {
            Long steamId64 = constant + id;
            return String.valueOf(steamId64);
        } else {
            return inputId;
        }
    }

    public String getId32(String inputId) {
        Long id = Long.parseLong(inputId);
        if (constant > id) {
            return inputId;
        } else {
            Long steamId32 = id - constant;
            return String.valueOf(steamId32);
        }
    }

    public static SteamIdConverter defaultInstance() {
        return new SteamIdConverter();
    }

}
