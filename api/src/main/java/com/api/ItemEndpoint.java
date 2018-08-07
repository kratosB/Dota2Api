package com.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dao.entity.Item;
import com.service.local.IItemService;
import com.service.steam.ISteamHeroItemService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Created on 2017/06/14.
 * 
 * @author zhiqiang bao
 */
@RestController
public class ItemEndpoint {

    private Logger logger = LoggerFactory.getLogger(HeroEndpoint.class);

    private IItemService itemServiceImpl;

    private ISteamHeroItemService steamHeroItemServiceImpl;

    @Autowired
    public ItemEndpoint(IItemService itemServiceImpl, ISteamHeroItemService steamHeroItemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;
        this.steamHeroItemServiceImpl = steamHeroItemServiceImpl;
    }

    @ApiOperation("获取所有item")
    @GetMapping(value = "/api/item/listAll")
    public List<Item> listAll() {
        logger.info("开始获取所有item数据");
        List<Item> itemList = itemServiceImpl.listAll();
        logger.info("结束获取所有item数据");
        return itemList;
    }

    @ApiOperation("根据itemId获取item")
    @GetMapping(value = "/api/item/findById")
    public Item findById(
            @ApiParam(name = "itemId", required = true, defaultValue = "1") @RequestParam(name = "itemId", defaultValue = "1") int itemId) {
        logger.info("开始根据itemId获取item，itemId = {}", itemId);
        Item item = itemServiceImpl.findById(itemId);
        logger.info("开始根据itemId获取item，item = {}", item);
        return item;
    }

    @ApiOperation("从steam更新item数据")
    @PostMapping(value = "/api/item/steam/updateItemData")
    public void updateItemData() {
        logger.info("开始从steam更新item数据");
        steamHeroItemServiceImpl.updateItemData();
        logger.info("开始从steam更新item数据");
    }

}
