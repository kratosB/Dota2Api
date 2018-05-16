package com.service;

import com.dao.entity.Hero;

import java.util.List;

/**
 * Created on 2018/5/16.
 * 
 * @author zhiqiang bao
 */
public interface IHeroService {

    /**
     * 从数据库中，获取所有hero的信息的列表
     * 
     * @return 所有hero的信息
     */
    List<Hero> listAll();

    /**
     * 从数据库中，根据heroId，获取对应的hero信息
     * 
     * @param id
     *            heroId
     * @return 对应的hero信息
     */
    Hero findById(int id);

    /**
     * 调用steam接口，更新hero表的数据
     */
    void updateHeroData();

}
