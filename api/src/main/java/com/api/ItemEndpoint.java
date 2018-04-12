package com.api;

import com.bean.heroitem.ItemsEntity;
import com.dao.entity.Item;
import com.service.ItemService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created on 2017/06/14.
 */
@RestController
public class ItemEndpoint {

    @Autowired
    private ItemService itemService;

    @ApiOperation("获取所有item")
    @RequestMapping(value = "/api/item/listAll", method = RequestMethod.GET)
    public List<Item> listAll() {
        return itemService.listAll();
    }

    @ApiOperation("根据id获取item")
    @RequestMapping(value = "/api/item/findById", method = RequestMethod.GET)
    public Item findById(
            @ApiParam(name = "id", required = true, defaultValue = "1") @RequestParam(name = "id", defaultValue = "1") int id) {
        return itemService.findById(id);
    }

    @ApiOperation("更新item数据")
    @PostMapping(value = "/api/item/updateItemData")
    public void updateItemData() {
        itemService.updateItemData();
    }

}
