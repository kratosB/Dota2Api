package com.service.steam.impl;

import com.config.Configuration;
import com.dao.HeroDao;
import com.dao.ItemDao;
import com.dao.entity.Hero;
import com.dao.entity.Item;
import com.fasterxml.jackson.databind.JsonNode;
import com.service.steam.ISteamHeroItemService;
import com.util.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2018/5/21.
 * 
 * @author zhiqiang bao
 */
@Service
public class SteamHeroItemServiceImpl implements ISteamHeroItemService {

    private HeroDao heroDao;

    private ItemDao itemDao;

    private Configuration configuration;

    @Autowired
    public SteamHeroItemServiceImpl(HeroDao heroDao, ItemDao itemDao, Configuration configuration) {
        this.heroDao = heroDao;
        this.itemDao = itemDao;
        this.configuration = configuration;
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

    @Override
    public void updateItemData() {
        String getItem = "GetGameItems/";
        String getHeroUrl = configuration.getIEconUrl() + getItem + configuration.getApiVersion() + configuration.getApiKey()
                + configuration.getApiAnd() + configuration.getApiLanguage();
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(getHeroUrl, String.class);
        JsonNode jsonNodes = JsonMapper.nonDefaultMapper().fromJson(response, JsonNode.class);
        JsonNode itemNodes = jsonNodes.findPath("items");
        // 获取数据库中的ItemList
        List<Item> itemList = itemDao.findAll();
        List<Integer> itemIdList = itemList.stream().map(Item::getId).collect(Collectors.toList());
        // 循环新增/更新
        List<Item> updateList = new ArrayList<>();
        itemNodes.forEach(itemNode -> {
            int itemId = itemNode.findValue("id").asInt();
            if (itemIdList.contains(itemId)) {
                Item hero = itemDao.findOne(itemId);
                hero.setName(itemNode.findValue("name").asText());
                hero.setLocalizedName(itemNode.findValue("localized_name").asText());
                updateList.add(hero);
            } else {
                Item hero = new Item();
                hero.setId(itemId);
                hero.setName(itemNode.findValue("name").asText());
                hero.setLocalizedName(itemNode.findValue("localized_name").asText());
                updateList.add(hero);
            }
        });
        itemDao.save(updateList);
    }
}
