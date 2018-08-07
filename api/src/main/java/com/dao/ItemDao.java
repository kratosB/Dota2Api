package com.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dao.entity.Item;

/**
 * Created on 2018/4/12.
 *
 * @author zhiqiang bao
 */
public interface ItemDao extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {

}
