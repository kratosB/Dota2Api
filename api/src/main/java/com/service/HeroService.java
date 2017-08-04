package com.service;

import com.bean.heroitem.Heroes;
import com.bean.heroitem.HeroesEntity;
import com.util.JsonMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created on 2017/06/14.
 */
@Service
public class HeroService {

    private String steamUrl = "http://api.steampowered.com/IEconDOTA2_570/";

    private String version = "v1/";

    private String key = "?key=EFA1E81676FCC47157EA871A67741EF5";

    private String str2 = "&";

    private String language = "language=zh";

    String path = "https://wiki.teamfortress.com/wiki/Category:WebAPI";

    String path2 = "https://wiki.teamfortress.com/wiki/WebAPI#Dota_2";

    public HeroesEntity listAll() {
        String getHero = "GetHeroes/";
        String getHeroUrl = steamUrl + getHero + version + key + str2 + language;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        HeroesEntity heroesEntity = JsonMapper.nonDefaultMapper().fromJson(response, HeroesEntity.class);
        return heroesEntity;
    }

    public String listById(int id) {
        HeroesEntity entity = listAll();
        Heroes heroes = entity.getResult();
        return heroes.getNameById(id);
    }

}
