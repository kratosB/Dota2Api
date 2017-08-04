package com.api;

import com.bean.heroitem.ItemsEntity;
import com.service.ItemService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created on 2017/06/14.
 */
@RestController
public class ItemEndpoint {

    @Autowired
    private ItemService itemService;

    @ApiOperation("获取所有item名称")
    @RequestMapping(value = "/api/item/listAll", method = RequestMethod.GET)
    public ItemsEntity listAll() {
        return itemService.listAll();
    }
}
