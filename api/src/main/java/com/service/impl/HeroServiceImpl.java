package com.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.config.Configuration;
import com.service.IHeroService;
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
public class HeroServiceImpl implements IHeroService{

    private HeroDao heroDao;

    private Configuration configuration;

    @Autowired
    public HeroServiceImpl(HeroDao heroDao, Configuration configuration) {
        this.heroDao = heroDao;
        this.configuration = configuration;
    }

    @Override
    public List<Hero> listAll() {
        return heroDao.findAll();
    }

    @Override
    public Hero findById(int id) {
        return heroDao.findOne(id);
    }

    @Override
    public void updateHeroData() {
        // 获取steam的hero数据
        String getHero = "GetHeroes/";
        String getHeroUrl = configuration.getIEconUrl() + getHero + configuration.getApiVersion() + configuration.getApiKey()
                + configuration.getApiAnd() + configuration.getApiLanguage();
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