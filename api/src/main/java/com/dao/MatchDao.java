package com.dao;

import com.dao.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created on 2018/5/11.
 * 
 * @author zhiqiang bao
 */
public interface MatchDao extends JpaRepository<Match, Long>, JpaSpecificationExecutor<Match> {
}
