package com.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dao.entity.Memo;

/**
 * Created on 2018/11/30.
 *
 * @author zhiqiang bao
 */
public interface MemoDao extends JpaRepository<Memo, Long>, JpaSpecificationExecutor<Memo> {
}
