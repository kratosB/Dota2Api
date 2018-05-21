package com.service.local.impl;

import com.dao.ItemDao;
import com.dao.entity.Item;
import com.service.local.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Item findById(int id) {
        return itemDao.findOne(id);
    }

}
