package com.dao;

import com.dao.entity.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created on 2018/5/11.
 *
 * @author zhiqiang bao
 */
public interface MatchPlayerDao extends JpaRepository<MatchPlayer, Long>, JpaSpecificationExecutor<MatchPlayer> {

    List<MatchPlayer> findByMatchId(Long matchId);
}
