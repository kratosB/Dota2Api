package com.dao;

import com.dao.entity.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created on 2018/5/3.
 * 
 * @author zhiqiang bao
 */
public interface RelationDao extends JpaRepository<Relation, Integer>, JpaSpecificationExecutor<Relation> {

    /**
     * 获取steamId的所有好友的数据
     * 
     * @param steamId
     *            用户id
     * @return 好友关系列表
     */
    List<Relation> findBySteamId(String steamId);
}
