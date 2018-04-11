package com.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bean.heroitem.ItemsEntity;
import com.util.JsonMapper;

/**
 * Created on 2017/06/14.
 */
@Service
public class ItemService {

    private String steamUrl = "http://api.steampowered.com/IEconDOTA2_570/";

    private String version = "v1/";

    private String key = "?key=EFA1E81676FCC47157EA871A67741EF5";

    private String str2 = "&";

    private String language = "language=zh";

    public ItemsEntity listAll() {
        String getItem = "GetGameItems/";
        String getHeroUrl = steamUrl + getItem + version + key + str2 + language;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        ItemsEntity entity = JsonMapper.nonDefaultMapper().fromJson(response, ItemsEntity.class);
        return entity;
    }

}
