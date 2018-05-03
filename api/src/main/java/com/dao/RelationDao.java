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

    List<Relation> findBySteamIdAndAndFriendIdIn(String steamId, List<String> friendIdList);
}
