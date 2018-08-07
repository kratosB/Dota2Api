package com.service.local.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.ItemDao;
import com.dao.entity.Item;
import com.service.local.IItemService;

/**
 * Created on 2017/06/14.
 * 
 * @author zhiqiang bao
 */
@Service
public class ItemServiceImpl implements IItemService {

    private ItemDao itemDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Override
    public List<Item> listAll() {
        return itemDao.findAll();
    }

    @Override
    public Item findById(int itemId) {
        return itemDao.findOne(itemId);
    }

}
