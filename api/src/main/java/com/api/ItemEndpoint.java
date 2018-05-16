package com.api;

import com.dao.entity.Item;
import com.service.IItemService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created on 2017/06/14.
 * 
 * @author zhiqiang bao
 */
@RestController
public class ItemEndpoint {

    private IItemService itemServiceImpl;

    @Autowired
    public ItemEndpoint(IItemService itemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;
    }

    @ApiOperation("获取所有item")
    @GetMapping(value = "/api/item/listAll")
    public List<Item> listAll() {
        return itemServiceImpl.listAll();
    }

    @ApiOperation("根据id获取item")
    @GetMapping(value = "/api/item/findById")
    public Item findById(
            @ApiParam(name = "id", required = true, defaultValue = "1") @RequestParam(name = "id", defaultValue = "1") int id) {
        return itemServiceImpl.findById(id);
    }

    @ApiOperation("从steam更新item数据")
    @PostMapping(value = "/api/item/updateItemData")
    public void updateItemData() {
        itemServiceImpl.updateItemData();
    }

}
