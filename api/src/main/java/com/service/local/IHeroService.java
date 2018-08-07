package com.service.local;

import java.util.List;

import com.dao.entity.Hero;

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
     * @param heroId
     *            heroId
     * @return 对应的hero信息
     */
    Hero findById(int heroId);

}
