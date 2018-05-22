package com.dao;

import com.dao.entity.MatchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created on 2018/5/11.
 * 
 * @author zhiqiang bao
 */
public interface MatchHistoryDao extends JpaRepository<MatchHistory, Long>, JpaSpecificationExecutor<MatchHistory> {

    /**
     * 获取所有update数据是null的matchHistory，这些是批量导入的matchId，没有详细数据，job会通过该查询获取matchId，然后定时获取详细数据
     * 
     * @param pageable
     *            分页参数
     * @return matchIdList
     */
    Page<MatchHistory> findByUpdatedTimeIsNullOrderByMatchIdDesc(Pageable pageable);
}
