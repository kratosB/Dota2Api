package com.service.local.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.HeroDao;
import com.dao.entity.Hero;
import com.service.local.IHeroService;

/**
 * Created on 2017/06/14.
 * 
 * @author zhiqiang bao
 */
@Service
public class HeroServiceImpl implements IHeroService {

    private HeroDao heroDao;

    @Autowired
    public HeroServiceImpl(HeroDao heroDao) {
        this.heroDao = heroDao;
    }

    @Override
    public List<Hero> listAll() {
        return heroDao.findAll();
    }

    @Override
    public Hero findById(int heroId) {
        return heroDao.findOne(heroId);
    }

}
