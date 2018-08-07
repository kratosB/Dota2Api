package com.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dao.entity.MatchPlayer;

/**
 * Created on 2018/5/11.
 *
 * @author zhiqiang bao
 */
public interface MatchPlayerDao extends JpaRepository<MatchPlayer, Long>, JpaSpecificationExecutor<MatchPlayer> {

    List<MatchPlayer> findByMatchId(Long matchId);
}
