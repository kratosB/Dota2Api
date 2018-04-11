package com.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dao.entity.Player;

/**
 * Created on 2018/3/28.
 * 
 * @author zhiqiang bao
 */
public interface PlayerDao extends JpaRepository<Player, String>, JpaSpecificationExecutor<Player> {
}
