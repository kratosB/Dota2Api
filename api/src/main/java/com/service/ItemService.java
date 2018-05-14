package com.service;

import com.config.Configuration;
import com.dao.ItemDao;
import com.dao.entity.Item;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bean.heroitem.ItemsEntity;
import com.util.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2017/06/14.
 */
@Service
public class ItemService {

    private ItemDao itemDao;

    private Configuration configuration;

    @Autowired
    public ItemService(ItemDao itemDao, Configuration configuration) {
        this.itemDao = itemDao;
        this.configuration = configuration;
    }

    public List<Item> listAll() {
        return itemDao.findAll();
    }

    public Item findById(int id) {
        return itemDao.findOne(id);
    }

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
