package com.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dao.entity.Hero;

/**
 * Created on 2018/3/20.
 *
 * @author zhiqiang bao
 */
public interface HeroDao extends JpaRepository<Hero, Integer>, JpaSpecificationExecutor<Hero> {

}
