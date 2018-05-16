package com.service;

import com.dao.entity.Item;

import java.util.List;

/**
 * Created on 2018/5/16.
 * 
 * @author zhiqiang bao
 */
public interface IItemService {

    /**
     * 从数据库中，获取所有item的信息的列表
     *
     * @return 所有hero的信息
     */
    List<Item> listAll();

    /**
     * 从数据库中，根据itemId，获取对应的hero信息
     *
     * @param id
     *            itemId
     * @return 对应的item信息
     */
    Item findById(int id);

    /**
     * 调用steam接口，更新item表的数据
     */
    void updateItemData();
}
