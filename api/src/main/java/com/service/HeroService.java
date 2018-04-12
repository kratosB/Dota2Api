package com.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dao.HeroDao;
import com.dao.entity.Hero;
import com.fasterxml.jackson.databind.JsonNode;
import com.util.JsonMapper;

/**
 * Created on 2017/06/14.
 * 
 * @author zhiqiang bao
 */
@Service
public class HeroService {

    private HeroDao heroDao;

    @Autowired
    public HeroService(HeroDao heroDao) {
        this.heroDao = heroDao;
    }

    public List<Hero> listAll() {
        return heroDao.findAll();
    }

    public Hero findById(int id) {
        return heroDao.findOne(id);
    }

    public void updateHeroData() {
        // 获取steam的hero数据
        String getHero = "GetHeroes/";
        String steamUrl = "http://api.steampowered.com/IEconDOTA2_570/";
        String version = "v1/";
        String key = "?key=EFA1E81676FCC47157EA871A67741EF5";
        String str2 = "&";
        String language = "language=zh";
        String getHeroUrl = steamUrl + getHero + version + key + str2 + language;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        JsonNode jsonNodes = JsonMapper.nonDefaultMapper().fromJson(response, JsonNode.class);
        JsonNode heroNodes = jsonNodes.findPath("heroes");
        // 获取数据库中的heroList
        List<Hero> heroList = heroDao.findAll();
        List<Integer> heroIdList = heroList.stream().map(Hero::getId).collect(Collectors.toList());
        // 循环新增/更新
        List<Hero> updateList = new ArrayList<>();
        heroNodes.forEach(heroNode -> {
            int heroId = heroNode.findValue("id").asInt();
            if (heroIdList.contains(heroId)) {
                Hero hero = heroDao.findOne(heroId);
                hero.setName(heroNode.findValue("name").asText());
                hero.setLocalizedName(heroNode.findValue("localized_name").asText());
                updateList.add(hero);
            } else {
                Hero hero = new Hero();
                hero.setId(heroId);
                hero.setName(heroNode.findValue("name").asText());
                hero.setLocalizedName(heroNode.findValue("localized_name").asText());
                updateList.add(hero);
            }
        });
        heroDao.save(updateList);
    }

}
